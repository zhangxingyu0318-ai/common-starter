package com.zxy.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.csrf.CsrfTokenService;
import com.zxy.security.token.TokenResolver;
import com.zxy.security.validation.TokenValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 处理浏览器认证 Cookie 的 Servlet 过滤器，并在非安全方法执行 CSRF 校验。
 * <p>由自动配置在 Bearer 过滤器后注册；仅对配置标识的浏览器客户端读取 access-token Cookie，成功后建立认证上下文。</p>
 * <p>同类组件包括 Spring Security 的 {@code CookieCsrfTokenRepository}、{@code CsrfFilter} 及 session Cookie 认证过滤器。</p>
 */
public class BrowserCookieAuthenticationFilter extends AbstractServletAuthenticationFilter {

    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    private final CsrfTokenService csrfTokenService;
    private final TokenResolver tokenResolver;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public BrowserCookieAuthenticationFilter(AuthTokenProperties properties,
                                             TokenValidationService tokenValidationService,
                                             TokenResolver tokenResolver,
                                             CsrfTokenService csrfTokenService,
                                             SecurityUserHeaderWriter headerWriter,
                                             ObjectMapper objectMapper) {
        super(properties, tokenValidationService, headerWriter, objectMapper);
        this.tokenResolver = tokenResolver;
        this.csrfTokenService = csrfTokenService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return super.shouldNotFilter(request)
                || tokenResolver.hasBearerToken(request.getHeader(properties.getAuth().getHeaderName()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String accessToken = readCookie(request, properties.getCookie().getAccessTokenName());
        if (!StringUtils.hasText(accessToken)) {
            authenticate(accessToken, request, response, filterChain);
            return;
        }
        if (!SAFE_METHODS.contains(request.getMethod()) && !isCsrfWhitelisted(request.getRequestURI())
                && !hasValidCsrfToken(request)) {
            writeError(response, HttpServletResponse.SC_FORBIDDEN, "CSRF 令牌无效或已过期");
            return;
        }
        authenticate(accessToken, request, response, filterChain);
    }

    private boolean hasValidCsrfToken(HttpServletRequest request) {
        String cookieToken = readCookie(request, properties.getCsrf().getCookieName());
        String headerToken = request.getHeader(properties.getCsrf().getHeaderName());
        return StringUtils.hasText(cookieToken)
                && StringUtils.hasText(headerToken)
                && MessageDigest.isEqual(cookieToken.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                headerToken.getBytes(java.nio.charset.StandardCharsets.UTF_8))
                && csrfTokenService.isValid(headerToken);
    }

    private boolean isCsrfWhitelisted(String path) {
        List<String> whitelist = properties.getCsrf().getWhitelist();
        return whitelist != null && whitelist.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
