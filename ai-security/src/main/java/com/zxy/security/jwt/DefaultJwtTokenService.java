package com.zxy.security.jwt;

import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.exception.TokenValidationException;
import com.zxy.security.model.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class DefaultJwtTokenService implements JwtTokenService {

    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_DISPLAY_NAME = "displayName";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    private final AuthTokenProperties properties;
    private final SecretKey secretKey;

    public DefaultJwtTokenService(AuthTokenProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateAccessToken(SecurityUser user) {
        return generateToken(user, TOKEN_TYPE_ACCESS, properties.getAccessTokenTtl().toSeconds());
    }

    @Override
    public String generateRefreshToken(SecurityUser user) {
        return generateToken(user, TOKEN_TYPE_REFRESH, properties.getRefreshTokenTtl().toSeconds());
    }

    @Override
    public SecurityUser parseAccessToken(String token) {
        return parse(token, TOKEN_TYPE_ACCESS);
    }

    @Override
    public SecurityUser parseRefreshToken(String token) {
        return parse(token, TOKEN_TYPE_REFRESH);
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            parseSignedClaims(token);
            return true;
        } catch (TokenValidationException ex) {
            return false;
        }
    }

    @Override
    public Instant getExpiration(String token) {
        Date expiration = parseSignedClaims(token).getPayload().getExpiration();
        if (expiration == null) {
            throw new TokenValidationException("令牌缺少过期时间");
        }
        return expiration.toInstant();
    }

    private String generateToken(SecurityUser user, String tokenType, long ttlSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(user.userId())
                .claim(CLAIM_USERNAME, user.username())
                .claim(CLAIM_DISPLAY_NAME, user.displayName())
                .claim(CLAIM_ROLES, user.roles())
                .claim(CLAIM_TOKEN_TYPE, tokenType)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(secretKey)
                .compact();
    }

    private SecurityUser parse(String token, String expectedType) {
        Claims claims = parseSignedClaims(token).getPayload();
        String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
        if (!expectedType.equals(tokenType)) {
            throw new TokenValidationException("令牌类型不正确");
        }
        List<String> roles = claims.get(CLAIM_ROLES, List.class);
        return new SecurityUser(
                claims.getSubject(),
                claims.get(CLAIM_USERNAME, String.class),
                claims.get(CLAIM_DISPLAY_NAME, String.class),
                roles == null ? List.of() : roles
        );
    }

    private Jws<Claims> parseSignedClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(properties.getIssuer())
                    .build()
                    .parseSignedClaims(token);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new TokenValidationException("令牌非法或已过期", ex);
        }
    }
}

