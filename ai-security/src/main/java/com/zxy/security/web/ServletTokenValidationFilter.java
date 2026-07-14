package com.zxy.security.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxy.core.Result;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.model.TokenValidationResult;
import com.zxy.security.token.TokenResolver;
import com.zxy.security.validation.TokenValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ServletTokenValidationFilter extends OncePerRequestFilter {

    private final AuthTokenProperties properties;
    private final TokenValidationService tokenValidationService;
    private final TokenResolver tokenResolver;
    private final SecurityUserHeaderWriter headerWriter;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public ServletTokenValidationFilter(AuthTokenProperties properties,
                                        TokenValidationService tokenValidationService,
                                        TokenResolver tokenResolver,
                                        SecurityUserHeaderWriter headerWriter,
                                        ObjectMapper objectMapper) {
        this.properties = properties;
        this.tokenValidationService = tokenValidationService;
        this.tokenResolver = tokenResolver;
        this.headerWriter = headerWriter;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!properties.isEnabled()) {
            return true;
        }
        List<String> excludePaths = properties.getExcludePaths();
        String path = request.getRequestURI();
        return excludePaths != null && excludePaths.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String headerValue = request.getHeader(properties.getHeaderName());
        String token = tokenResolver.resolve(headerValue);
        TokenValidationResult validationResult = tokenValidationService.validateAccessToken(token);
        if (!validationResult.valid()) {
            writeUnauthorized(response, validationResult.message());
            return;
        }
        filterChain.doFilter(headerWriter.write(request, validationResult.user()), response);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), Result.fail("401", normalize(message), normalize(message)));
    }

    private String normalize(String message) {
        return StringUtils.hasText(message) ? message : "未授权访问";
    }
}

