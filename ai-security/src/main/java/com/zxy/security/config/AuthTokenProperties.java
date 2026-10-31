package com.zxy.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * ai-security 的统一外部配置模型，绑定 {@code zxy.security} 前缀。
 * <p>用于配置 JWT 签发/校验、令牌黑名单、Cookie、CSRF、CORS 和网关向下游透传的身份请求头；
 * 由 {@link com.zxy.security.autoconfigure.AiSecurityAutoConfiguration} 创建的各组件注入使用，业务服务和
 * {@code ai-getway} 都可通过 application.yml 覆盖默认值。</p>
 * <p>同类方案包括 Spring Boot 的 {@code @ConfigurationProperties} 配置对象，以及 Spring Security 的
 * {@code OAuth2ResourceServerProperties}、{@code CorsProperties}；本类将本平台相关配置集中管理。</p>
 */
@Getter
@Setter
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
    private List<String> excludePaths = new ArrayList<>(List.of("/actuator/**", "/api/auth/login", "/api/auth/refresh", "/error"));
    private Auth auth = new Auth();
    private Cookie cookie = new Cookie();
    private IdentityHeaders identityHeaders = new IdentityHeaders();

    /**
     * 浏览器 CSRF 防护配置。
     * <p>由 {@link com.zxy.security.csrf.CsrfTokenService} 及 Servlet/WebFlux 浏览器 Cookie 认证链路使用，
     * 负责双提交令牌的名称、签名密钥、有效期和豁免路径。</p>
     * <p>同类组件包括 Spring Security 的 {@code CsrfTokenRepository} 与 {@code CookieCsrfTokenRepository}。</p>
     */
    @Getter
    @Setter
    public static class Csrf {
        private String cookieName = "XSRF-TOKEN";
        private String headerName = "X-CSRF-TOKEN";
        private String secret;
        private Duration tokenTtl = Duration.ofHours(2);
        private List<String> whitelist = new ArrayList<>();
    }

    /**
     * JWT 与请求认证配置。
     * <p>由 {@link com.zxy.security.jwt.DefaultJwtTokenService}、{@link com.zxy.security.token.TokenResolver} 和
     * 各认证过滤器使用，定义令牌签名、签发方、有效期、请求头及无需认证的路径。</p>
     * <p>同类配置包括 Spring Security OAuth2 Resource Server 的 JWT 配置及自定义 {@code JwtDecoder} 配置。</p>
     */
    @Getter
    @Setter
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
    }

    /**
     * 浏览器认证 Cookie 配置。
     * <p>由 {@link com.zxy.security.web.AuthenticationCookieService} 在认证中心写入或清理 access/refresh Cookie，
     * 并由浏览器 Cookie 认证过滤器读取。</p>
     * <p>同类机制包括 Servlet {@code Cookie}、Spring {@code ResponseCookie} 及 Spring Session 的 Cookie 配置。</p>
     */
    @Getter
    @Setter
    public static class Cookie {
        private String accessTokenName = "AI_ACCESS_TOKEN";
        private String refreshTokenName = "AI_REFRESH_TOKEN";
        private String path = "/";
        private String sameSite = "Lax";
        private boolean secure;
    }

    /**
     * 网关向下游业务服务传递已认证用户身份的请求头配置。
     * <p>由 {@link com.zxy.security.web.SecurityUserHeaderWriter} 在令牌校验成功后写入用户 ID、用户名、显示名和角色。</p>
     * <p>同类方案包括 Spring Security {@code SecurityContext} 传递、OAuth2 Token Relay，或内部签名身份头。</p>
     */
    @Getter
    @Setter
    public static class IdentityHeaders {
        private String userId = "X-User-Id";
        private String username = "X-Username";
        private String displayName = "X-Display-Name";
        private String roles = "X-User-Roles";
    }

    /**
     * 跨域资源共享（CORS）配置。
     * <p>供认证中心、网关或业务服务的 Web 层按允许来源、方法、请求头和路径建立跨域策略；当前属性模型为统一配置入口。</p>
     * <p>同类组件包括 Spring MVC 的 {@code CorsRegistry}、{@code CorsConfiguration} 和 WebFlux 的 {@code CorsWebFilter}。</p>
     */
    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>();
        private List<String> allowedMethods = new ArrayList<>(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        private List<String> allowedHeaders = new ArrayList<>(List.of("Authorization", "Content-Type", "X-CSRF-TOKEN", "X-Auth-Client"));
        private List<String> exposedHeaders = new ArrayList<>();
        private List<String> pathWhitelist = new ArrayList<>(List.of("/**"));
        private boolean allowCredentials = true;
    }
}

