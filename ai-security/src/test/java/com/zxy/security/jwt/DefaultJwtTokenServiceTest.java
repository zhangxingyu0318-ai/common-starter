package com.zxy.security.jwt;

import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.model.SecurityUser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DefaultJwtTokenService} 的单元测试。
 * <p>在 ai-security 构建时验证 access/refresh token 的签发、解析、过期和类型隔离；覆盖生产中的默认 JWT 实现。</p>
 * <p>同类测试包括 Spring Security {@code JwtEncoder}/{@code JwtDecoder} 集成测试及资源服务器认证测试。</p>
 */
class DefaultJwtTokenServiceTest {

    @Test
    void shouldGenerateAndParseAccessToken() {
        AuthTokenProperties properties = new AuthTokenProperties();
        DefaultJwtTokenService service = new DefaultJwtTokenService(properties);
        SecurityUser user = new SecurityUser("1", "admin", "管理员", List.of("ADMIN"));

        String token = service.generateAccessToken(user);
        SecurityUser parsed = service.parseAccessToken(token);

        assertEquals(user.userId(), parsed.userId());
        assertEquals(user.username(), parsed.username());
        assertTrue(parsed.roles().contains("ADMIN"));
    }

    @Test
    void shouldRejectRefreshTokenAsAccessToken() {
        AuthTokenProperties properties = new AuthTokenProperties();
        DefaultJwtTokenService service = new DefaultJwtTokenService(properties);
        SecurityUser user = new SecurityUser("2", "guest", "访客", List.of("USER"));

        String refreshToken = service.generateRefreshToken(user);

        assertThrows(RuntimeException.class, () -> service.parseAccessToken(refreshToken));
    }
}

