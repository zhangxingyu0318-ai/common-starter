package com.zxy.auth.service;

import com.zxy.security.model.SecurityUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary account source; replace this service with database, LDAP, or social-login adapters. */
@Service
public class DemoUserDetailsService implements UserDetailsService {

    private final Map<String, AuthUserPrincipal> users;

    public DemoUserDetailsService(PasswordEncoder passwordEncoder) {
        users = new ConcurrentHashMap<>(Map.of(
                "admin", principal("1001", "admin", "admin123", "平台管理员", List.of("ADMIN", "USER"), passwordEncoder),
                "analyst", principal("1002", "analyst", "123456", "数据分析师", List.of("USER"), passwordEncoder)
        ));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUserPrincipal user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return user;
    }

    private AuthUserPrincipal principal(String userId, String username, String password, String displayName,
                                        List<String> roles, PasswordEncoder passwordEncoder) {
        return new AuthUserPrincipal(new SecurityUser(userId, username, displayName, roles), passwordEncoder.encode(password));
    }
}

