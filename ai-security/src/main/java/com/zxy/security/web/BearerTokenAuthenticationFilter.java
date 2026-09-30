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

/** Authenticates mobile/API requests carrying an explicit Bearer token. */
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
        return super.shouldNotFilter(request) || !tokenResolver.hasBearerToken(request.getHeader(properties.getHeaderName()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        authenticate(tokenResolver.resolveBearerToken(request.getHeader(properties.getHeaderName())), request, response, filterChain);
    }
}


