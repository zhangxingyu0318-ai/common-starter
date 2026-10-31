package com.zxy.security.blacklist;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/**
 * 基于 Redis Key 存在性判断的令牌黑名单实现。
 * <p>由自动配置在容器存在 {@code StringRedisTemplate} 时作为 {@link TokenBlacklistChecker} 默认实现，
 * 被 {@link com.zxy.security.validation.TokenValidationService} 用于各服务的本地令牌撤销检查。</p>
 * <p>同类实现包括 {@link NoopTokenBlacklistChecker}，以及可按需实现的数据库、Caffeine 或远程鉴权中心检查器。</p>
 */
public class RedisTokenBlacklistChecker implements TokenBlacklistChecker {

	private final StringRedisTemplate redisTemplate;
	private final String keyPrefix;

	public RedisTokenBlacklistChecker(StringRedisTemplate redisTemplate, String keyPrefix) {
		this.redisTemplate = redisTemplate;
		this.keyPrefix = keyPrefix;
	}

	@Override
	public boolean isBlacklisted(String token) {
		if (!StringUtils.hasText(token)) {
			return false;
		}
		Boolean exists = redisTemplate.hasKey(keyPrefix + token);
		return Boolean.TRUE.equals(exists);
	}
}
