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

