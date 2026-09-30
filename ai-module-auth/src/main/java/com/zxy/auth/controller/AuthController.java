package com.zxy.auth.controller;

import com.zxy.auth.dto.LoginRequest;
import com.zxy.auth.dto.LogoutRequest;
import com.zxy.auth.dto.RefreshTokenRequest;
import com.zxy.auth.model.TokenResponse;
import com.zxy.auth.model.TokenValidationResponse;
import com.zxy.auth.model.UserInfoResponse;
import com.zxy.auth.service.AuthService;
import com.zxy.core.BusinessException;
import com.zxy.core.Result;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.csrf.CsrfTokenService;
import com.zxy.security.web.AuthenticationCookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX = "Bearer ";

    private final AuthService authService;
    private final AuthTokenProperties properties;
    private final AuthenticationCookieService cookieService;
    private final CsrfTokenService csrfTokenService;

    public AuthController(AuthService authService, AuthTokenProperties properties,
                          AuthenticationCookieService cookieService, CsrfTokenService csrfTokenService) {
        this.authService = authService;
        this.properties = properties;
        this.cookieService = cookieService;
        this.csrfTokenService = csrfTokenService;
    }

    @PostMapping("/login")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest,
                                       HttpServletResponse servletResponse) {
        TokenResponse response = authService.login(request);
        if (isBrowserRequest(servletRequest)) {
            cookieService.writeLoginCookies(servletResponse, response.accessToken(), response.refreshToken());
            response = withoutTokens(response);
        }
        return Result.success(response, "登录成功", "Login success");
    }

    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(@Valid @RequestBody(required = false) RefreshTokenRequest request,
                                         HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        boolean browserRequest = isBrowserRequest(servletRequest);
        if (browserRequest && !hasValidCsrfToken(servletRequest)) {
            throw new BusinessException("403", "CSRF 令牌无效或已过期");
        }
        String refreshToken = browserRequest ? cookieService.readCookie(servletRequest, properties.getCookie().getRefreshTokenName())
                : request == null ? null : request.refreshToken();
        TokenResponse response = authService.refresh(refreshToken);
        if (browserRequest) {
            cookieService.writeLoginCookies(servletResponse, response.accessToken(), response.refreshToken());
            response = withoutTokens(response);
        }
        return Result.success(response, "刷新成功", "Refresh success");
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestBody(required = false) LogoutRequest request,
                               @RequestHeader(value = AUTHORIZATION, required = false) String authorization,
                               HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String accessToken = extractToken(authorization);
        String refreshToken = request != null ? request.refreshToken() : null;
        if (!StringUtils.hasText(accessToken) && request != null) {
            accessToken = request.accessToken();
        }
        if (!StringUtils.hasText(accessToken)) {
            accessToken = cookieService.readCookie(servletRequest, properties.getCookie().getAccessTokenName());
        }
        if (!StringUtils.hasText(refreshToken)) {
            refreshToken = cookieService.readCookie(servletRequest, properties.getCookie().getRefreshTokenName());
        }
        authService.logout(accessToken, refreshToken);
        cookieService.clearLoginCookies(servletResponse);
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
            token = cookieService.readCookie(request, properties.getCookie().getAccessTokenName());
        }
        return Result.success(authService.currentUser(token), "查询成功", "Current user success");
    }

    private String extractToken(String authorization) {
        if (!StringUtils.hasText(authorization)) {
            return null;
        }
        return authorization.regionMatches(true, 0, PREFIX, 0, PREFIX.length())
                ? authorization.substring(PREFIX.length()).trim() : null;
    }

    private boolean isBrowserRequest(HttpServletRequest request) {
        return properties.getAuth().getBrowserClientType().equalsIgnoreCase(request.getHeader(properties.getAuth().getClientTypeHeader()))
                || StringUtils.hasText(cookieService.readCookie(request, properties.getCookie().getAccessTokenName()))
                || StringUtils.hasText(cookieService.readCookie(request, properties.getCookie().getRefreshTokenName()));
    }

    private boolean hasValidCsrfToken(HttpServletRequest request) {
        String cookieToken = cookieService.readCookie(request, properties.getCsrf().getCookieName());
        String headerToken = request.getHeader(properties.getCsrf().getHeaderName());
        return StringUtils.hasText(cookieToken) && StringUtils.hasText(headerToken)
                && MessageDigest.isEqual(cookieToken.getBytes(StandardCharsets.UTF_8), headerToken.getBytes(StandardCharsets.UTF_8))
                && csrfTokenService.isValid(headerToken);
    }

    private TokenResponse withoutTokens(TokenResponse response) {
        return new TokenResponse(null, null, response.tokenType(), response.expiresInSeconds(), response.userId(),
                response.username(), response.displayName(), response.roles());
    }
}

