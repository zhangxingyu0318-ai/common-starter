package com.zxy.security.csrf;

import com.zxy.security.config.AuthTokenProperties;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link CsrfTokenService} 的单元测试。
 * <p>在 ai-security 构建时验证本平台 CSRF 令牌生成、签名校验和有效期规则，覆盖浏览器 Cookie 认证链路的基础能力。</p>
 * <p>同类测试包括 Spring Security {@code CsrfTokenRepository} 与 {@code CsrfFilter} 的 Web 层集成测试。</p>
 */
class CsrfTokenServiceTest {

    @Test
    void shouldValidateUntamperedToken() {
        CsrfTokenService service = new CsrfTokenService(new AuthTokenProperties());
        assertTrue(service.isValid(service.generateToken()));
    }

    @Test
    void shouldRejectTamperedOrExpiredToken() {
        AuthTokenProperties properties = new AuthTokenProperties();
        properties.getCsrf().setTokenTtl(Duration.ofSeconds(-1));
        CsrfTokenService service = new CsrfTokenService(properties);
        String token = service.generateToken();

        assertFalse(service.isValid(token + "x"));
        assertFalse(service.isValid(token));
    }
}
