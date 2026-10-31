package com.zxy.security.webflux;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxy.core.Result;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.csrf.CsrfTokenService;
import com.zxy.security.token.TokenResolver;
import com.zxy.security.validation.TokenValidationService;
import com.zxy.security.web.SecurityUserHeaderWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.HttpCookie;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;

/**
 * 面向 WebFlux 的非阻塞访问令牌认证过滤器。
 * <p>由自动配置在网关或其他响应式应用中注册，完成 Bearer/Cookie 令牌读取、CSRF 校验、身份头透传和未授权响应；
 * 核心 JWT 与黑名单校验逻辑复用 {@link com.zxy.security.validation.TokenValidationService}。</p>
 * <p>同类组件包括 Spring WebFlux 的 {@code WebFilter}、Spring Security 的 {@code AuthenticationWebFilter}。</p>
 */
public class ReactiveTokenValidationFilter implements WebFilter {

    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    private final AuthTokenProperties properties;
    private final TokenValidationService tokenValidationService;
    private final TokenResolver tokenResolver;
    private final CsrfTokenService csrfTokenService;
    private final SecurityUserHeaderWriter headerWriter;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public ReactiveTokenValidationFilter(AuthTokenProperties properties,
                                         TokenValidationService tokenValidationService,
                                         TokenResolver tokenResolver,
                                         CsrfTokenService csrfTokenService,
                                         SecurityUserHeaderWriter headerWriter,
                                         ObjectMapper objectMapper) {
        this.properties = properties;
        this.tokenValidationService = tokenValidationService;
        this.tokenResolver = tokenResolver;
        this.csrfTokenService = csrfTokenService;
        this.headerWriter = headerWriter;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!properties.getAuth().isEnabled() || isExcluded(exchange.getRequest().getPath().value())) {
            return chain.filter(exchange);
        }
        String headerValue = exchange.getRequest().getHeaders().getFirst(properties.getAuth().getHeaderName());
        String token = tokenResolver.hasBearerToken(headerValue)
                ? tokenResolver.resolveBearerToken(headerValue)
                : cookieValue(exchange, properties.getCookie().getAccessTokenName());
        if (!tokenResolver.hasBearerToken(headerValue) && StringUtils.hasText(token)
                && !SAFE_METHODS.contains(exchange.getRequest().getMethod().name())
                && !isCsrfWhitelisted(exchange.getRequest().getPath().value())
                && !hasValidCsrfToken(exchange)) {
            return writeError(exchange.getResponse(), HttpStatus.FORBIDDEN, "CSRF 令牌无效或已过期");
        }
        return Mono.fromCallable(() -> tokenValidationService.validateAccessToken(token))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(result -> {
                    if (!result.valid()) {
                        return writeError(exchange.getResponse(), HttpStatus.UNAUTHORIZED, result.message());
                    }
                    ServerHttpRequest mutatedRequest = headerWriter.write(exchange.getRequest(), result.user());
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    private boolean isExcluded(String path) {
        List<String> excludePaths = properties.getAuth().getWhitelist();
        return excludePaths != null && excludePaths.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private boolean hasValidCsrfToken(ServerWebExchange exchange) {
        String cookieToken = cookieValue(exchange, properties.getCsrf().getCookieName());
        String headerToken = exchange.getRequest().getHeaders().getFirst(properties.getCsrf().getHeaderName());
        return cookieToken != null && headerToken != null
                && MessageDigest.isEqual(cookieToken.getBytes(StandardCharsets.UTF_8), headerToken.getBytes(StandardCharsets.UTF_8))
                && csrfTokenService.isValid(headerToken);
    }

    private boolean isCsrfWhitelisted(String path) {
        List<String> whitelist = properties.getCsrf().getWhitelist();
        return whitelist != null && whitelist.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private String cookieValue(ServerWebExchange exchange, String name) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(name);
        return cookie == null ? null : cookie.getValue();
    }

    private Mono<Void> writeError(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = toBytes(Result.fail(String.valueOf(status.value()), message, message));
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    private byte[] toBytes(Object value) {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException ex) {
            return ("{\"code\":\"500\",\"msgCn\":\"序列化失败\",\"msgEn\":\"Serialize failed\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }
    }
}
