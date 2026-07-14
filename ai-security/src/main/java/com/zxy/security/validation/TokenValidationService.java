package com.zxy.security.validation;

import com.zxy.security.blacklist.TokenBlacklistChecker;
import com.zxy.security.exception.TokenValidationException;
import com.zxy.security.jwt.JwtTokenService;
import com.zxy.security.model.SecurityUser;
import com.zxy.security.model.TokenValidationResult;
import org.springframework.util.StringUtils;

public class TokenValidationService {

    private final JwtTokenService jwtTokenService;
    private final TokenBlacklistChecker tokenBlacklistChecker;

    public TokenValidationService(JwtTokenService jwtTokenService, TokenBlacklistChecker tokenBlacklistChecker) {
        this.jwtTokenService = jwtTokenService;
        this.tokenBlacklistChecker = tokenBlacklistChecker;
    }

    public TokenValidationResult validateAccessToken(String token) {
        if (!StringUtils.hasText(token)) {
            return TokenValidationResult.failure("未提供访问令牌");
        }
        if (tokenBlacklistChecker.isBlacklisted(token)) {
            return TokenValidationResult.failure("令牌已失效");
        }
        try {
            SecurityUser user = jwtTokenService.parseAccessToken(token);
            return TokenValidationResult.success(user);
        } catch (TokenValidationException ex) {
            return TokenValidationResult.failure(ex.getMessage());
        }
    }
}

