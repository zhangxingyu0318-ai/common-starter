package com.zxy.security.jwt;

import com.zxy.security.model.SecurityUser;

import java.time.Instant;

/**
 * JWT 的签发、解析、有效性检查及过期时间查询契约。
 * <p>认证中心通过它签发 access/refresh token，{@link com.zxy.security.validation.TokenValidationService} 和刷新令牌流程
 * 通过它解析相应类型的令牌。</p>
 * <p>当前实现为 {@link DefaultJwtTokenService}；同类方案包括 Spring Security 的 {@code JwtEncoder}/{@code JwtDecoder}
 * 或 OAuth2 授权服务器提供的令牌服务。</p>
 */
public interface JwtTokenService {

    String generateAccessToken(SecurityUser user);

    String generateRefreshToken(SecurityUser user);

    SecurityUser parseAccessToken(String token);

    SecurityUser parseRefreshToken(String token);

    boolean isTokenValid(String token);

    Instant getExpiration(String token);
}

