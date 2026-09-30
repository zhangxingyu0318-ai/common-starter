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
    private Csrf csrf = new Csrf();
    private Cors cors = new Cors();
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
    private Auth auth = new Auth();
    private Cookie cookie = new Cookie();
    private IdentityHeaders identityHeaders = new IdentityHeaders();

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

    public Csrf getCsrf() {
        return csrf;
    }

    public void setCsrf(Csrf csrf) {
        this.csrf = csrf;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public Auth getAuth() { return auth; }
    public void setAuth(Auth auth) { this.auth = auth; }
    public Cookie getCookie() { return cookie; }
    public void setCookie(Cookie cookie) { this.cookie = cookie; }
    public IdentityHeaders getIdentityHeaders() { return identityHeaders; }
    public void setIdentityHeaders(IdentityHeaders identityHeaders) { this.identityHeaders = identityHeaders; }

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

    public static class Csrf {
        private String cookieName = "XSRF-TOKEN";
        private String headerName = "X-CSRF-TOKEN";
        private String secret;
        private Duration tokenTtl = Duration.ofHours(2);
        private List<String> whitelist = new ArrayList<>();

        public String getCookieName() { return cookieName; }
        public void setCookieName(String cookieName) { this.cookieName = cookieName; }
        public String getHeaderName() { return headerName; }
        public void setHeaderName(String headerName) { this.headerName = headerName; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public Duration getTokenTtl() { return tokenTtl; }
        public void setTokenTtl(Duration tokenTtl) { this.tokenTtl = tokenTtl; }
        public List<String> getWhitelist() { return whitelist; }
        public void setWhitelist(List<String> whitelist) { this.whitelist = whitelist; }
    }

    public static class Auth {
        private boolean enabled = true;
        private String secret = "change-this-demo-secret-to-a-long-32-byte-minimum-key";
        private String issuer = "common-starter";
        private Duration accessTokenTtl = Duration.ofMinutes(30);
        private Duration refreshTokenTtl = Duration.ofDays(7);
        private String headerName = "Authorization";
        private String tokenPrefix = "Bearer";
        private String blacklistPrefix = "auth:blacklist:";
        private String clientTypeHeader = "X-Auth-Client";
        private String browserClientType = "browser";
        private List<String> whitelist = new ArrayList<>(List.of("/actuator/**", "/api/auth/login", "/api/auth/refresh", "/error"));

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public String getIssuer() { return issuer; }
        public void setIssuer(String issuer) { this.issuer = issuer; }
        public Duration getAccessTokenTtl() { return accessTokenTtl; }
        public void setAccessTokenTtl(Duration accessTokenTtl) { this.accessTokenTtl = accessTokenTtl; }
        public Duration getRefreshTokenTtl() { return refreshTokenTtl; }
        public void setRefreshTokenTtl(Duration refreshTokenTtl) { this.refreshTokenTtl = refreshTokenTtl; }
        public String getHeaderName() { return headerName; }
        public void setHeaderName(String headerName) { this.headerName = headerName; }
        public String getTokenPrefix() { return tokenPrefix; }
        public void setTokenPrefix(String tokenPrefix) { this.tokenPrefix = tokenPrefix; }
        public String getBlacklistPrefix() { return blacklistPrefix; }
        public void setBlacklistPrefix(String blacklistPrefix) { this.blacklistPrefix = blacklistPrefix; }
        public String getClientTypeHeader() { return clientTypeHeader; }
        public void setClientTypeHeader(String clientTypeHeader) { this.clientTypeHeader = clientTypeHeader; }
        public String getBrowserClientType() { return browserClientType; }
        public void setBrowserClientType(String browserClientType) { this.browserClientType = browserClientType; }
        public List<String> getWhitelist() { return whitelist; }
        public void setWhitelist(List<String> whitelist) { this.whitelist = whitelist; }
    }

    public static class Cookie {
        private String accessTokenName = "AI_ACCESS_TOKEN";
        private String refreshTokenName = "AI_REFRESH_TOKEN";
        private String path = "/";
        private String sameSite = "Lax";
        private boolean secure;

        public String getAccessTokenName() { return accessTokenName; }
        public void setAccessTokenName(String accessTokenName) { this.accessTokenName = accessTokenName; }
        public String getRefreshTokenName() { return refreshTokenName; }
        public void setRefreshTokenName(String refreshTokenName) { this.refreshTokenName = refreshTokenName; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getSameSite() { return sameSite; }
        public void setSameSite(String sameSite) { this.sameSite = sameSite; }
        public boolean isSecure() { return secure; }
        public void setSecure(boolean secure) { this.secure = secure; }
    }

    public static class IdentityHeaders {
        private String userId = "X-User-Id";
        private String username = "X-Username";
        private String displayName = "X-Display-Name";
        private String roles = "X-User-Roles";

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String displayName) { this.displayName = displayName; }
        public String getRoles() { return roles; }
        public void setRoles(String roles) { this.roles = roles; }
    }

    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        private List<String> allowedHeaders = new ArrayList<>(List.of("Authorization", "Content-Type", "X-CSRF-TOKEN", "X-Auth-Client"));
        private List<String> exposedHeaders = new ArrayList<>();
        private List<String> pathWhitelist = new ArrayList<>(List.of("/**"));
        private boolean allowCredentials = true;

        public List<String> getAllowedOrigins() { return allowedOrigins; }
        public void setAllowedOrigins(List<String> allowedOrigins) { this.allowedOrigins = allowedOrigins; }
        public List<String> getAllowedMethods() { return allowedMethods; }
        public void setAllowedMethods(List<String> allowedMethods) { this.allowedMethods = allowedMethods; }
        public List<String> getAllowedHeaders() { return allowedHeaders; }
        public void setAllowedHeaders(List<String> allowedHeaders) { this.allowedHeaders = allowedHeaders; }
        public List<String> getExposedHeaders() { return exposedHeaders; }
        public void setExposedHeaders(List<String> exposedHeaders) { this.exposedHeaders = exposedHeaders; }
        public List<String> getPathWhitelist() { return pathWhitelist; }
        public void setPathWhitelist(List<String> pathWhitelist) { this.pathWhitelist = pathWhitelist; }
        public boolean isAllowCredentials() { return allowCredentials; }
        public void setAllowCredentials(boolean allowCredentials) { this.allowCredentials = allowCredentials; }
    }
}

