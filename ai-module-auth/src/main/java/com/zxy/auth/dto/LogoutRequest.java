package com.zxy.auth.dto;

public record LogoutRequest(
        String accessToken,
        String refreshToken
) {
}

