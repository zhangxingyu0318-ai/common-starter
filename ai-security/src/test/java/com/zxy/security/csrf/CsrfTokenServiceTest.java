package com.zxy.security.csrf;

import com.zxy.security.config.AuthTokenProperties;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsrfTokenServiceTest {

    @Test
    void shouldValidateUntamperedToken() {
        CsrfTokenService service = new CsrfTokenService(new AuthTokenProperties());
        assertTrue(service.isValid(service.generateToken()));
    }

    @Test
    void shouldRejectTamperedOrExpiredToken() {
        AuthTokenProperties properties = new AuthTokenProperties();
        properties.setCsrfTokenTtl(Duration.ofSeconds(-1));
        CsrfTokenService service = new CsrfTokenService(properties);
        String token = service.generateToken();

        assertFalse(service.isValid(token + "x"));
        assertFalse(service.isValid(token));
    }
}


