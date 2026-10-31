package com.zxy.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 已由令牌确认身份的用户不可变数据模型。
 * <p>由 {@link com.zxy.security.jwt.JwtTokenService} 从 JWT 声明构建，随后被校验结果、认证上下文和身份请求头写入器使用。</p>
 * <p>同类模型包括 Spring Security 的 {@code UserDetails}、{@code Jwt} 的 claims 与 {@code Principal}。</p>
 */
public record SecurityUser(String userId, String username, String displayName, List<String> roles) {

    public SecurityUser {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }

    public List<GrantedAuthority> toAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(Objects::nonNull)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}
