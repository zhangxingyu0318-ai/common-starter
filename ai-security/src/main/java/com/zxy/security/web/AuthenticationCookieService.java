package com.zxy.security.web;

import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.csrf.CsrfTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;

/** Issues and clears browser authentication cookies without server-side session state. */
public class AuthenticationCookieService {

    private final AuthTokenProperties properties;
    private final CsrfTokenService csrfTokenService;

    public AuthenticationCookieService(AuthTokenProperties properties, CsrfTokenService csrfTokenService) {
        this.properties = properties;
        this.csrfTokenService = csrfTokenService;
    }

    public void writeLoginCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        add(response, properties.getAccessTokenCookieName(), accessToken, properties.getAccessTokenTtl(), true);
        add(response, properties.getRefreshTokenCookieName(), refreshToken, properties.getRefreshTokenTtl(), true);
        add(response, properties.getCsrfCookieName(), csrfTokenService.generateToken(), properties.getCsrfTokenTtl(), false);
    }

    public void clearLoginCookies(HttpServletResponse response) {
        clear(response, properties.getAccessTokenCookieName(), true);
        clear(response, properties.getRefreshTokenCookieName(), true);
        clear(response, properties.getCsrfCookieName(), false);
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
                .httpOnly(httpOnly).secure(properties.isCookieSecure()).path(properties.getCookiePath())
                .sameSite(properties.getCookieSameSite()).maxAge(ttl).build().toString());
    }

    private void clear(HttpServletResponse response, String name, boolean httpOnly) {
        response.addHeader("Set-Cookie", ResponseCookie.from(name, "")
                .httpOnly(httpOnly).secure(properties.isCookieSecure()).path(properties.getCookiePath())
                .sameSite(properties.getCookieSameSite()).maxAge(java.time.Duration.ZERO).build().toString());
    }
}

