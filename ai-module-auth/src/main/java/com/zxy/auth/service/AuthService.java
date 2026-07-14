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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final JwtTokenService jwtTokenService;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final AuthTokenProperties properties;
    private final TokenValidationService tokenValidationService;
    private final Map<String, DemoUserAccount> accounts = new ConcurrentHashMap<>();

    public AuthService(JwtTokenService jwtTokenService,
                       PasswordEncoder passwordEncoder,
                       StringRedisTemplate redisTemplate,
                       AuthTokenProperties properties,
                       TokenValidationService tokenValidationService) {
        this.jwtTokenService = jwtTokenService;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.tokenValidationService = tokenValidationService;
        initDemoUsers();
    }

    public TokenResponse login(LoginRequest request) {
        DemoUserAccount account = accounts.get(request.username());
        if (account == null || !passwordEncoder.matches(request.password(), account.passwordHash())) {
            throw new BusinessException("401", "用户名或密码错误");
        }
        return issueTokens(account.user());
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
                properties.getAccessTokenTtl().toSeconds(),
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
        Boolean exists = redisTemplate.hasKey(properties.getBlacklistPrefix() + token);
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
            redisTemplate.opsForValue().set(properties.getBlacklistPrefix() + token, "1", ttl);
        } catch (RuntimeException ignored) {
            // 忽略非法或已过期令牌的黑名单写入
        }
    }

    private void initDemoUsers() {
        register("1001", "admin", "admin123", "平台管理员", List.of("ADMIN", "USER"));
        register("1002", "analyst", "123456", "数据分析师", List.of("USER"));
    }

    private void register(String userId, String username, String rawPassword, String displayName, List<String> roles) {
        SecurityUser user = new SecurityUser(userId, username, displayName, roles);
        accounts.put(username, new DemoUserAccount(user, passwordEncoder.encode(rawPassword)));
    }

    private record DemoUserAccount(SecurityUser user, String passwordHash) {
    }
}

