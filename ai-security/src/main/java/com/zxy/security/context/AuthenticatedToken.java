package com.zxy.security.context;

import com.zxy.security.model.SecurityUser;

/** The authenticated request state available during a servlet request. */
public record AuthenticatedToken(String token, SecurityUser user) {
}

