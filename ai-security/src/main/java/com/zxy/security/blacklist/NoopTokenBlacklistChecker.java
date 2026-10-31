package com.zxy.security.blacklist;

/**
 * 不执行撤销检查的黑名单占位实现。
 * <p>当应用未配置 Redis 且未提供自定义 {@link TokenBlacklistChecker} Bean 时由自动配置兜底创建，
 * 使 JWT 签名与过期校验仍可正常运行。</p>
 * <p>同类实现包括生产环境优先使用的 {@link RedisTokenBlacklistChecker}，或自定义的数据库/远程检查器。</p>
 */
public class NoopTokenBlacklistChecker implements TokenBlacklistChecker {

	@Override
	public boolean isBlacklisted(String token) {
		return false;
	}
}
