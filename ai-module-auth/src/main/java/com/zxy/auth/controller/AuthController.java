package com.zxy.auth.controller;

import com.zxy.auth.dto.LoginRequest;
import com.zxy.auth.dto.LogoutRequest;
import com.zxy.auth.dto.RefreshTokenRequest;
import com.zxy.auth.model.TokenResponse;
import com.zxy.auth.model.TokenValidationResponse;
import com.zxy.auth.model.UserInfoResponse;
import com.zxy.auth.service.AuthService;
import com.zxy.core.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request), "登录成功", "Login success");
    }

    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return Result.success(authService.refresh(request.refreshToken()), "刷新成功", "Refresh success");
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestBody(required = false) LogoutRequest request,
                               @RequestHeader(value = AUTHORIZATION, required = false) String authorization) {
        String accessToken = extractToken(authorization);
        String refreshToken = request != null ? request.refreshToken() : null;
        if (!StringUtils.hasText(accessToken) && request != null) {
            accessToken = request.accessToken();
        }
        authService.logout(accessToken, refreshToken);
        return Result.ok();
    }

    @GetMapping("/validate")
    public Result<TokenValidationResponse> validate(@RequestHeader(value = AUTHORIZATION, required = false) String authorization) {
        String token = extractToken(authorization);
        return Result.success(authService.validate(token), "校验成功", "Validate success");
    }

    @GetMapping("/me")
    public Result<UserInfoResponse> currentUser(HttpServletRequest request,
                                                @RequestHeader(value = AUTHORIZATION, required = false) String authorization) {
        String token = extractToken(authorization);
        if (!StringUtils.hasText(token)) {
            token = extractToken(request.getHeader(AUTHORIZATION));
        }
        return Result.success(authService.currentUser(token), "查询成功", "Current user success");
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.startsWith(PREFIX) ? authorization.substring(PREFIX.length()) : authorization;
    }
}

