package com.zxy.security.model;

/**
 * 一次访问令牌校验的不可变结果。
 * <p>由 {@link com.zxy.security.validation.TokenValidationService} 返回，Servlet 与 WebFlux 过滤器根据成功状态决定写入身份信息或返回未授权响应。</p>
 * <p>同类模型包括 Spring Security 的 {@code Authentication}、{@code OAuth2TokenValidatorResult} 与函数式 {@code Result} 类型。</p>
 */
public record TokenValidationResult(
        boolean valid,
        String message,
        SecurityUser user
) {

    public static TokenValidationResult success(SecurityUser user) {
        return new TokenValidationResult(true, "OK", user);
    }

    public static TokenValidationResult failure(String message) {
        return new TokenValidationResult(false, message, null);
    }
}
