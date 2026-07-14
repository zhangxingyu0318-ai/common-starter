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

public class SecurityUserHeaderWriter {

    private final AuthTokenProperties properties;

    public SecurityUserHeaderWriter(AuthTokenProperties properties) {
        this.properties = properties;
    }

    public ServerHttpRequest write(ServerHttpRequest request, SecurityUser user) {
        return request.mutate()
                .header(properties.getUserIdHeader(), defaultValue(user.userId()))
                .header(properties.getUsernameHeader(), defaultValue(user.username()))
                .header(properties.getDisplayNameHeader(), defaultValue(user.displayName()))
                .header(properties.getRolesHeader(), String.join(",", user.roles()))
                .build();
    }

    public HttpServletRequest write(HttpServletRequest request, SecurityUser user) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(properties.getUserIdHeader(), defaultValue(user.userId()));
        headers.put(properties.getUsernameHeader(), defaultValue(user.username()));
        headers.put(properties.getDisplayNameHeader(), defaultValue(user.displayName()));
        headers.put(properties.getRolesHeader(), String.join(",", user.roles()));
        return new MutableHeaderHttpServletRequest(request, headers);
    }

    private String defaultValue(String value) {
        return StringUtils.hasText(value) ? value : "";
    }

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

