package com.zxy.security.token;

import com.zxy.security.config.AuthTokenProperties;
import org.springframework.util.StringUtils;

/**
 * 从 HTTP 请求头或 Cookie 中按平台配置提取原始访问令牌。
 * <p>由 Servlet 和 WebFlux 认证过滤器调用，以统一 Bearer Header 与浏览器 Cookie 两种传输方式的解析规则。</p>
 * <p>同类组件包括 Spring Security 的 {@code BearerTokenResolver}、{@code DefaultBearerTokenResolver}。</p>
 */
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
                && headerValue.regionMatches(true, 0, properties.getAuth().getTokenPrefix() + " ", 0,
                properties.getAuth().getTokenPrefix().length() + 1)
                && StringUtils.hasText(resolveBearerToken(headerValue));
    }

    public String resolveBearerToken(String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return null;
        }
        String prefix = properties.getAuth().getTokenPrefix() + " ";
        return headerValue.regionMatches(true, 0, prefix, 0, prefix.length())
                ? headerValue.substring(prefix.length()).trim() : null;
    }
}
