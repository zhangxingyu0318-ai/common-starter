package com.zxy.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.token.TokenResolver;
import com.zxy.security.validation.TokenValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理非浏览器 Bearer Authorization 请求的 Servlet 认证过滤器。
 * <p>由自动配置以高优先级注册；从配置指定请求头提取 access token，校验后建立 Spring Security 上下文并透传用户身份头。</p>
 * <p>同类过滤器包括 {@code BearerTokenAuthenticationFilter}（Spring Security Resource Server）和自定义 {@code OncePerRequestFilter}。</p>
 */
public class BearerTokenAuthenticationFilter extends AbstractServletAuthenticationFilter {

    private final TokenResolver tokenResolver;

    public BearerTokenAuthenticationFilter(AuthTokenProperties properties,
                                           TokenValidationService tokenValidationService,
                                           TokenResolver tokenResolver,
                                           SecurityUserHeaderWriter headerWriter,
                                           ObjectMapper objectMapper) {
        super(properties, tokenValidationService, headerWriter, objectMapper);
        this.tokenResolver = tokenResolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return super.shouldNotFilter(request) || !tokenResolver.hasBearerToken(request.getHeader(properties.getAuth().getHeaderName()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        authenticate(tokenResolver.resolveBearerToken(request.getHeader(properties.getAuth().getHeaderName())), request, response, filterChain);
    }
}
