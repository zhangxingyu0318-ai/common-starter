package com.zxy.security.context;

import com.zxy.security.model.SecurityUser;

/**
 * Servlet-request scoped convenience context. Callers must not retain its value beyond the request.
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

