package com.zxy.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "zxy.security")
public class AuthTokenProperties {

    private boolean enabled = true;
    private String secret = "change-this-demo-secret-to-a-long-32-byte-minimum-key";
    private String issuer = "common-starter";
    private Duration accessTokenTtl = Duration.ofMinutes(30);
    private Duration refreshTokenTtl = Duration.ofDays(7);
    private String headerName = "Authorization";
    private String tokenPrefix = "Bearer";
    private String blacklistPrefix = "auth:blacklist:";
    private String accessTokenCookieName = "AI_ACCESS_TOKEN";
    private String refreshTokenCookieName = "AI_REFRESH_TOKEN";
    private String csrfCookieName = "XSRF-TOKEN";
    private String csrfHeaderName = "X-CSRF-TOKEN";
    private String csrfSecret;
    private Duration csrfTokenTtl = Duration.ofHours(2);
    private String cookiePath = "/";
    private String cookieSameSite = "Lax";
    private boolean cookieSecure;
    private String clientTypeHeader = "X-Auth-Client";
    private String browserClientType = "browser";
    private List<String> allowedOrigins = new ArrayList<>();
    private String userIdHeader = "X-User-Id";
    private String usernameHeader = "X-Username";
    private String displayNameHeader = "X-Display-Name";
    private String rolesHeader = "X-User-Roles";
    private List<String> excludePaths = new ArrayList<>(List.of(
            "/actuator/**",
            "/api/auth/login",
            "/api/auth/refresh",
            "/error"
    ));

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Duration getAccessTokenTtl() {
        return accessTokenTtl;
    }

    public void setAccessTokenTtl(Duration accessTokenTtl) {
        this.accessTokenTtl = accessTokenTtl;
    }

    public Duration getRefreshTokenTtl() {
        return refreshTokenTtl;
    }

    public void setRefreshTokenTtl(Duration refreshTokenTtl) {
        this.refreshTokenTtl = refreshTokenTtl;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public String getBlacklistPrefix() {
        return blacklistPrefix;
    }

    public void setBlacklistPrefix(String blacklistPrefix) {
        this.blacklistPrefix = blacklistPrefix;
    }

    public String getAccessTokenCookieName() {
        return accessTokenCookieName;
    }

    public void setAccessTokenCookieName(String accessTokenCookieName) {
        this.accessTokenCookieName = accessTokenCookieName;
    }

    public String getRefreshTokenCookieName() {
        return refreshTokenCookieName;
    }

    public void setRefreshTokenCookieName(String refreshTokenCookieName) {
        this.refreshTokenCookieName = refreshTokenCookieName;
    }

    public String getCsrfCookieName() {
        return csrfCookieName;
    }

    public void setCsrfCookieName(String csrfCookieName) {
        this.csrfCookieName = csrfCookieName;
    }

    public String getCsrfHeaderName() {
        return csrfHeaderName;
    }

    public void setCsrfHeaderName(String csrfHeaderName) {
        this.csrfHeaderName = csrfHeaderName;
    }

    public String getCsrfSecret() {
        return csrfSecret;
    }

    public void setCsrfSecret(String csrfSecret) {
        this.csrfSecret = csrfSecret;
    }

    public Duration getCsrfTokenTtl() {
        return csrfTokenTtl;
    }

    public void setCsrfTokenTtl(Duration csrfTokenTtl) {
        this.csrfTokenTtl = csrfTokenTtl;
    }

    public String getCookiePath() {
        return cookiePath;
    }

    public void setCookiePath(String cookiePath) {
        this.cookiePath = cookiePath;
    }

    public String getCookieSameSite() {
        return cookieSameSite;
    }

    public void setCookieSameSite(String cookieSameSite) {
        this.cookieSameSite = cookieSameSite;
    }

    public boolean isCookieSecure() {
        return cookieSecure;
    }

    public void setCookieSecure(boolean cookieSecure) {
        this.cookieSecure = cookieSecure;
    }

    public String getClientTypeHeader() {
        return clientTypeHeader;
    }

    public void setClientTypeHeader(String clientTypeHeader) {
        this.clientTypeHeader = clientTypeHeader;
    }

    public String getBrowserClientType() {
        return browserClientType;
    }

    public void setBrowserClientType(String browserClientType) {
        this.browserClientType = browserClientType;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getUserIdHeader() {
        return userIdHeader;
    }

    public void setUserIdHeader(String userIdHeader) {
        this.userIdHeader = userIdHeader;
    }

    public String getUsernameHeader() {
        return usernameHeader;
    }

    public void setUsernameHeader(String usernameHeader) {
        this.usernameHeader = usernameHeader;
    }

    public String getDisplayNameHeader() {
        return displayNameHeader;
    }

    public void setDisplayNameHeader(String displayNameHeader) {
        this.displayNameHeader = displayNameHeader;
    }

    public String getRolesHeader() {
        return rolesHeader;
    }

    public void setRolesHeader(String rolesHeader) {
        this.rolesHeader = rolesHeader;
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }
}

