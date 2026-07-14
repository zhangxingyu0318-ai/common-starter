package com.zxy.security.token;

import com.zxy.security.config.AuthTokenProperties;
import org.springframework.util.StringUtils;

public class TokenResolver {

    private final AuthTokenProperties properties;

    public TokenResolver(AuthTokenProperties properties) {
        this.properties = properties;
    }

    public String resolve(String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }
        String prefix = properties.getTokenPrefix() + " ";
        if (headerValue.startsWith(prefix)) {
            return headerValue.substring(prefix.length());
        }
        return headerValue;
    }
}

