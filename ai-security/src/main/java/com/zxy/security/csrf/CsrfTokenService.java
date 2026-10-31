package com.zxy.security.csrf;

import com.zxy.security.config.AuthTokenProperties;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * 提供浏览器 Cookie 认证所需的 CSRF 令牌生成与校验。
 * <p>由 {@link com.zxy.security.web.AuthenticationCookieService} 写入 CSRF Cookie，并由浏览器 Servlet 过滤器和
 * WebFlux 过滤器在状态变更请求中验证请求头携带的令牌。</p>
 * <p>同类组件包括 Spring Security 的 {@code CsrfTokenRepository}、{@code CookieCsrfTokenRepository} 与 {@code CsrfFilter}。</p>
 */
public class CsrfTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final AuthTokenProperties properties;

    public CsrfTokenService(AuthTokenProperties properties) {
        this.properties = properties;
    }

    public String generateToken() {
        String payload = UUID.randomUUID() + "." + Instant.now().getEpochSecond();
        return encode(payload) + "." + encode(sign(payload));
    }

    public boolean isValid(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        String[] parts = token.split("\\.", -1);
        if (parts.length != 2) {
            return false;
        }
        try {
            String payload = decode(parts[0]);
            byte[] actualSignature = Base64.getUrlDecoder().decode(parts[1]);
            if (!MessageDigest.isEqual(sign(payload), actualSignature)) {
                return false;
            }
            String[] payloadParts = payload.split("\\.", -1);
            if (payloadParts.length != 2) {
                return false;
            }
            UUID.fromString(payloadParts[0]);
            long issuedAt = Long.parseLong(payloadParts[1]);
            long now = Instant.now().getEpochSecond();
            return issuedAt <= now && now - issuedAt <= properties.getCsrf().getTokenTtl().toSeconds();
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private byte[] sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            String secret = StringUtils.hasText(properties.getCsrf().getSecret())
                    ? properties.getCsrf().getSecret() : properties.getAuth().getSecret();
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("无法初始化 CSRF HMAC", ex);
        }
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String encode(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}




