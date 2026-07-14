package com.zxy.security.jwt;

import com.zxy.security.model.SecurityUser;

import java.time.Instant;

public interface JwtTokenService {

    String generateAccessToken(SecurityUser user);

    String generateRefreshToken(SecurityUser user);

    SecurityUser parseAccessToken(String token);

    SecurityUser parseRefreshToken(String token);

    boolean isTokenValid(String token);

    Instant getExpiration(String token);
}

