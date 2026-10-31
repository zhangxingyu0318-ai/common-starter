package com.zxy.security.blacklist;

/**
 * 查询令牌是否已被撤销的抽象契约。
 * <p>由 {@link com.zxy.security.validation.TokenValidationService} 在 JWT 签名校验后调用，认证中心登出时负责写入的
 * 黑名单可立即被网关和业务服务识别。</p>
 * <p>同类实现包括 {@link RedisTokenBlacklistChecker}、{@link NoopTokenBlacklistChecker}，也可替换为数据库、缓存或远程鉴权服务实现。</p>
 */
public interface TokenBlacklistChecker {

    boolean isBlacklisted(String token);
}
