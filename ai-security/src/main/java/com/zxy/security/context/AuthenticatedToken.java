package com.zxy.security.context;

import com.zxy.security.model.SecurityUser;

/**
 * 将已验证的原始令牌与用户身份关联的不可变值对象。
 * <p>由认证过滤器在校验成功后构建，并保存到 {@link AuthenticationTokenContext}，供同一请求链路的代码获取。</p>
 * <p>同类对象包括 Spring Security 的 {@code Authentication}、{@code BearerTokenAuthentication}。</p>
 */
public record AuthenticatedToken(String token, SecurityUser user) {
}
