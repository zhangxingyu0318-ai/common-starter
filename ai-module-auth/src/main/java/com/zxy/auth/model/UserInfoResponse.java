package com.zxy.auth.model;

import java.util.List;

public record UserInfoResponse(
        String userId,
        String username,
        String displayName,
        List<String> roles
) {
}

