package com.zxy.security.web;

import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.csrf.CsrfTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

/**
 * 集中创建与清理浏览器认证、刷新令牌及 CSRF Cookie 的服务。
 * <p>由 {@code ai-module-auth} 的登录、刷新和登出接口调用，Cookie 属性取自 {@link AuthTokenProperties.Cookie} 与 CSRF 配置。</p>
 * <p>同类方案包括 Spring 的 {@code ResponseCookie}、Spring Session 的 Cookie 编码器和直接操作 Servlet {@code Cookie}。</p>
 */
public class AuthenticationCookieService {

    private final AuthTokenProperties properties;
    private final CsrfTokenService csrfTokenService;

    public AuthenticationCookieService(AuthTokenProperties properties, CsrfTokenService csrfTokenService) {
        this.properties = properties;
        this.csrfTokenService = csrfTokenService;
    }

    public void writeLoginCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        add(response, properties.getCookie().getAccessTokenName(), accessToken, properties.getAuth().getAccessTokenTtl(), true);
        add(response, properties.getCookie().getRefreshTokenName(), refreshToken, properties.getAuth().getRefreshTokenTtl(), true);
        add(response, properties.getCsrf().getCookieName(), csrfTokenService.generateToken(), properties.getCsrf().getTokenTtl(), false);
    }

    public void clearLoginCookies(HttpServletResponse response) {
        clear(response, properties.getCookie().getAccessTokenName(), true);
        clear(response, properties.getCookie().getRefreshTokenName(), true);
        clear(response, properties.getCsrf().getCookieName(), false);
    }

    public String readCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies).filter(cookie -> name.equals(cookie.getName()))
                .map(Cookie::getValue).findFirst().orElse(null);
    }

    private void add(HttpServletResponse response, String name, String value, java.time.Duration ttl, boolean httpOnly) {
        response.addHeader("Set-Cookie", ResponseCookie.from(name, value)
                .httpOnly(httpOnly).secure(properties.getCookie().isSecure()).path(properties.getCookie().getPath())
                .sameSite(properties.getCookie().getSameSite()).maxAge(ttl).build().toString());
    }

    private void clear(HttpServletResponse response, String name, boolean httpOnly) {
        response.addHeader("Set-Cookie", ResponseCookie.from(name, "")
                .httpOnly(httpOnly).secure(properties.getCookie().isSecure()).path(properties.getCookie().getPath())
                .sameSite(properties.getCookie().getSameSite()).maxAge(java.time.Duration.ZERO).build().toString());
    }
}
