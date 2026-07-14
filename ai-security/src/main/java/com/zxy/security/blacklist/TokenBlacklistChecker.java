package com.zxy.security.blacklist;

public interface TokenBlacklistChecker {

    boolean isBlacklisted(String token);
}

