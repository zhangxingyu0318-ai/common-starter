package com.zxy.security.blacklist;

public class NoopTokenBlacklistChecker implements TokenBlacklistChecker {

	@Override
	public boolean isBlacklisted(String token) {
		return false;
	}
}

