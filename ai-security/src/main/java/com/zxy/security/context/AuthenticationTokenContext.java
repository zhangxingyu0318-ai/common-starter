package com.zxy.security.context;

import com.zxy.security.model.SecurityUser;

/**
 * 基于 {@link ThreadLocal} 保存当前 Servlet 请求认证结果的上下文工具类。
 * <p>由 Servlet 认证过滤器在校验通过时设置、请求结束时清理，供未直接依赖 Spring Security Context 的业务代码读取。</p>
 * <p>同类机制包括 Spring Security 的 {@code SecurityContextHolder}；在 WebFlux 场景应使用 Reactor {@code Context} 而非 ThreadLocal。</p>
 */
public final class AuthenticationTokenContext {

    private static final ThreadLocal<AuthenticatedToken> HOLDER = new ThreadLocal<>();

    private AuthenticationTokenContext() {
    }

    public static void set(String token, SecurityUser user) {
        HOLDER.set(new AuthenticatedToken(token, user));
    }

    public static AuthenticatedToken get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
