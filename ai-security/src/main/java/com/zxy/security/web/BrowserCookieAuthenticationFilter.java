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

/** Authenticates browser requests with an HttpOnly JWT cookie and HMAC-protected CSRF token. */
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





