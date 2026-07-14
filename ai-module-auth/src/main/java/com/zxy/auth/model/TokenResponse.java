package com.zxy.auth.model;

import java.util.List;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        String userId,
        String username,
        String displayName,
        List<String> roles
) {
}

