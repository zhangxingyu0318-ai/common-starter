package com.zxy.security.web;

import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.model.SecurityUser;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 将已认证用户信息写入当前请求头，供网关后的业务服务消费。
 * <p>由 Servlet 认证过滤器和 {@link com.zxy.security.webflux.ReactiveTokenValidationFilter} 在令牌校验成功后调用；
 * 请求头名称由 {@link AuthTokenProperties.IdentityHeaders} 统一配置。</p>
 * <p>同类方案包括 Spring Security {@code SecurityContextHolder}、OAuth2 Token Relay，或签名的内部身份令牌。</p>
 */
public class SecurityUserHeaderWriter {

    private final AuthTokenProperties properties;

    public SecurityUserHeaderWriter(AuthTokenProperties properties) {
        this.properties = properties;
    }

    public ServerHttpRequest write(ServerHttpRequest request, SecurityUser user) {
        return request.mutate()
                .header(properties.getIdentityHeaders().getUserId(), defaultValue(user.userId()))
                .header(properties.getIdentityHeaders().getUsername(), defaultValue(user.username()))
                .header(properties.getIdentityHeaders().getDisplayName(), defaultValue(user.displayName()))
                .header(properties.getIdentityHeaders().getRoles(), String.join(",", user.roles()))
                .build();
    }

    public HttpServletRequest write(HttpServletRequest request, SecurityUser user) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(properties.getIdentityHeaders().getUserId(), defaultValue(user.userId()));
        headers.put(properties.getIdentityHeaders().getUsername(), defaultValue(user.username()));
        headers.put(properties.getIdentityHeaders().getDisplayName(), defaultValue(user.displayName()));
        headers.put(properties.getIdentityHeaders().getRoles(), String.join(",", user.roles()));
        return new MutableHeaderHttpServletRequest(request, headers);
    }

    private String defaultValue(String value) {
        return StringUtils.hasText(value) ? value : "";
    }

    /**
     * 可附加身份请求头的 Servlet 请求包装器。
     * <p>仅由外层 {@link SecurityUserHeaderWriter} 在 Servlet 过滤器链内创建，避免修改原始容器请求对象。</p>
     * <p>同类类型包括 {@link HttpServletRequestWrapper} 及 Spring 的各类 request wrapper。</p>
     */
    private static final class MutableHeaderHttpServletRequest extends HttpServletRequestWrapper {

        private final Map<String, String> headers;

        private MutableHeaderHttpServletRequest(HttpServletRequest request, Map<String, String> headers) {
            super(request);
            this.headers = headers;
        }

        @Override
        public String getHeader(String name) {
            String value = headers.get(name);
            return value != null ? value : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Map<String, String> combined = new LinkedHashMap<>();
            Enumeration<String> baseNames = super.getHeaderNames();
            while (baseNames.hasMoreElements()) {
                String name = baseNames.nextElement();
                combined.put(name, super.getHeader(name));
            }
            combined.putAll(headers);
            return Collections.enumeration(combined.keySet());
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String header = getHeader(name);
            return Collections.enumeration(header == null ? Collections.emptyList() : Collections.singletonList(header));
        }
    }
}

