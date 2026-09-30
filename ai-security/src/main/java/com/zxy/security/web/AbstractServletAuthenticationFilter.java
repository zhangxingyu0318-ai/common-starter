package com.zxy.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxy.core.Result;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.context.AuthenticationTokenContext;
import com.zxy.security.model.TokenValidationResult;
import com.zxy.security.validation.TokenValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

abstract class AbstractServletAuthenticationFilter extends OncePerRequestFilter {

    protected final AuthTokenProperties properties;
    private final TokenValidationService tokenValidationService;
    private final SecurityUserHeaderWriter headerWriter;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    protected AbstractServletAuthenticationFilter(AuthTokenProperties properties,
                                                  TokenValidationService tokenValidationService,
                                                  SecurityUserHeaderWriter headerWriter,
                                                  ObjectMapper objectMapper) {
        this.properties = properties;
        this.tokenValidationService = tokenValidationService;
        this.headerWriter = headerWriter;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !properties.isEnabled() || isExcluded(request.getRequestURI());
    }

    protected boolean isExcluded(String path) {
        List<String> excludePaths = properties.getExcludePaths();
        return excludePaths != null && excludePaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    protected boolean authenticate(String token, HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws IOException, ServletException {
        TokenValidationResult result = tokenValidationService.validateAccessToken(token);
        if (!result.valid()) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, result.message());
            return false;
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                result.user(), null, result.user().toAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthenticationTokenContext.set(token, result.user());
        try {
            filterChain.doFilter(headerWriter.write(request, result.user()), response);
        } finally {
            AuthenticationTokenContext.clear();
            SecurityContextHolder.clearContext();
        }
        return true;
    }

    protected void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        String normalized = StringUtils.hasText(message) ? message : (status == 403 ? "CSRF 校验失败" : "未授权访问");
        objectMapper.writeValue(response.getWriter(), Result.fail(String.valueOf(status), normalized, normalized));
    }
}

