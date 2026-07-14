package com.zxy.security.blacklist;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

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

