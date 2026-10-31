package com.zxy.security.exception;

/**
 * 表示 JWT 格式、签名、签发方、有效期或令牌类型不符合要求的领域异常。
 * <p>由 {@link com.zxy.security.jwt.DefaultJwtTokenService} 抛出，并由 {@link com.zxy.security.validation.TokenValidationService}
 * 转换为失败校验结果，供 Web 过滤器产生统一未授权响应。</p>
 * <p>同类异常包括 Spring Security 的 {@code InvalidBearerTokenException} 与 {@code JwtException}。</p>
 */
public class TokenValidationException extends RuntimeException {

    public TokenValidationException(String message) {
        super(message);
    }

    public TokenValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
