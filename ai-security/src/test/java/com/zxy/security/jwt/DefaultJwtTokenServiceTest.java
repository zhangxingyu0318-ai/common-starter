package com.zxy.security.jwt;

import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.model.SecurityUser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

