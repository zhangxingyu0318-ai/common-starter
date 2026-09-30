package com.zxy.security.token;

import com.zxy.security.config.AuthTokenProperties;
import org.springframework.util.StringUtils;

public class TokenResolver {

    private final AuthTokenProperties properties;

    public TokenResolver(AuthTokenProperties properties) {
        this.properties = properties;
    }

    public String resolve(String headerValue) {
        return hasBearerToken(headerValue) ? resolveBearerToken(headerValue) : null;
    }

    public boolean hasBearerToken(String headerValue) {
        return StringUtils.hasText(headerValue)
                && headerValue.regionMatches(true, 0, properties.getTokenPrefix() + " ", 0,
                properties.getTokenPrefix().length() + 1)
                && StringUtils.hasText(resolveBearerToken(headerValue));
    }

    public String resolveBearerToken(String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }
        String prefix = properties.getTokenPrefix() + " ";
        return headerValue.regionMatches(true, 0, prefix, 0, prefix.length())
                ? headerValue.substring(prefix.length()).trim() : null;
    }
}

