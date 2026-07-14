package com.zxy.security.webflux;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxy.core.Result;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.model.TokenValidationResult;
import com.zxy.security.token.TokenResolver;
import com.zxy.security.validation.TokenValidationService;
import com.zxy.security.web.SecurityUserHeaderWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReactiveTokenValidationFilter implements WebFilter {

    private final AuthTokenProperties properties;
    private final TokenValidationService tokenValidationService;
    private final TokenResolver tokenResolver;
    private final SecurityUserHeaderWriter headerWriter;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public ReactiveTokenValidationFilter(AuthTokenProperties properties,
                                         TokenValidationService tokenValidationService,
                                         TokenResolver tokenResolver,
                                         SecurityUserHeaderWriter headerWriter,
                                         ObjectMapper objectMapper) {
        this.properties = properties;
        this.tokenValidationService = tokenValidationService;
        this.tokenResolver = tokenResolver;
        this.headerWriter = headerWriter;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!properties.isEnabled() || isExcluded(exchange.getRequest().getPath().value())) {
            return chain.filter(exchange);
        }
        String headerValue = exchange.getRequest().getHeaders().getFirst(properties.getHeaderName());
        String token = tokenResolver.resolve(headerValue);
        return Mono.fromCallable(() -> tokenValidationService.validateAccessToken(token))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(result -> {
                    if (!result.valid()) {
                        return writeUnauthorized(exchange.getResponse(), result.message());
                    }
                    ServerHttpRequest mutatedRequest = headerWriter.write(exchange.getRequest(), result.user());
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    private boolean isExcluded(String path) {
        List<String> excludePaths = properties.getExcludePaths();
        return excludePaths != null && excludePaths.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    private Mono<Void> writeUnauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = toBytes(Result.fail("401", message, message));
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


