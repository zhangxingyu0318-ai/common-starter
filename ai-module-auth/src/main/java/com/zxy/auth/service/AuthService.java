package com.zxy.auth.service;

import com.zxy.auth.dto.LoginRequest;
import com.zxy.auth.model.TokenResponse;
import com.zxy.auth.model.TokenValidationResponse;
import com.zxy.auth.model.UserInfoResponse;
import com.zxy.core.BusinessException;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.jwt.JwtTokenService;
import com.zxy.security.model.SecurityUser;
import com.zxy.security.model.TokenValidationResult;
import com.zxy.security.validation.TokenValidationService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class AuthService {

    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;
    private final AuthTokenProperties properties;
    private final TokenValidationService tokenValidationService;

    public AuthService(JwtTokenService jwtTokenService,
                       AuthenticationManager authenticationManager,
                       StringRedisTemplate redisTemplate,
                       AuthTokenProperties properties,
                       TokenValidationService tokenValidationService) {
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.tokenValidationService = tokenValidationService;
    }

    public TokenResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
            AuthUserPrincipal principal = (AuthUserPrincipal) authentication.getPrincipal();
            return issueTokens(principal.securityUser());
        } catch (AuthenticationException | ClassCastException ex) {
            throw new BusinessException("401", "用户名或密码错误");
        }
    }

    public TokenResponse refresh(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) || isBlacklisted(refreshToken)) {
            throw new BusinessException("401", "刷新令牌无效或已失效");
        }
        SecurityUser user = jwtTokenService.parseRefreshToken(refreshToken);
        blacklistToken(refreshToken);
        return issueTokens(user);
    }

    public void logout(String accessToken, String refreshToken) {
        blacklistToken(accessToken);
        blacklistToken(refreshToken);
    }

    public UserInfoResponse currentUser(String accessToken) {
        TokenValidationResult validationResult = tokenValidationService.validateAccessToken(accessToken);
        if (!validationResult.valid()) {
            throw new BusinessException("401", validationResult.message());
        }
        SecurityUser user = validationResult.user();
        return new UserInfoResponse(user.userId(), user.username(), user.displayName(), user.roles());
    }

    public TokenValidationResponse validate(String accessToken) {
        TokenValidationResult validationResult = tokenValidationService.validateAccessToken(accessToken);
        if (!validationResult.valid()) {
            return new TokenValidationResponse(false, validationResult.message(), null, null, null, List.of());
        }
        SecurityUser user = validationResult.user();
        return new TokenValidationResponse(
                true,
                "令牌有效",
                user.userId(),
                user.username(),
                user.displayName(),
                user.roles()
        );
    }

    private TokenResponse issueTokens(SecurityUser user) {
        String accessToken = jwtTokenService.generateAccessToken(user);
        String refreshToken = jwtTokenService.generateRefreshToken(user);
        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                properties.getAuth().getAccessTokenTtl().toSeconds(),
                user.userId(),
                user.username(),
                user.displayName(),
                user.roles()
        );
    }

    private boolean isBlacklisted(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }
        Boolean exists = redisTemplate.hasKey(properties.getAuth().getBlacklistPrefix() + token);
        return Boolean.TRUE.equals(exists);
    }

    private void blacklistToken(String token) {
        if (!StringUtils.hasText(token)) {
            return;
        }
        try {
            Instant expiration = jwtTokenService.getExpiration(token);
            Duration ttl = Duration.between(Instant.now(), expiration);
            if (ttl.isNegative() || ttl.isZero()) {
                return;
            }
            redisTemplate.opsForValue().set(properties.getAuth().getBlacklistPrefix() + token, "1", ttl);
        } catch (RuntimeException ignored) {
            // 忽略非法或已过期令牌的黑名单写入
        }
    }

}

