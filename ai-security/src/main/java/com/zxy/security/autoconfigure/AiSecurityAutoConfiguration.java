package com.zxy.security.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxy.security.blacklist.NoopTokenBlacklistChecker;
import com.zxy.security.blacklist.RedisTokenBlacklistChecker;
import com.zxy.security.blacklist.TokenBlacklistChecker;
import com.zxy.security.config.AuthTokenProperties;
import com.zxy.security.jwt.DefaultJwtTokenService;
import com.zxy.security.jwt.JwtTokenService;
import com.zxy.security.token.TokenResolver;
import com.zxy.security.validation.TokenValidationService;
import com.zxy.security.web.SecurityUserHeaderWriter;
import com.zxy.security.web.ServletTokenValidationFilter;
import com.zxy.security.webflux.ReactiveTokenValidationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.WebFilter;

@AutoConfiguration
@EnableConfigurationProperties(AuthTokenProperties.class)
public class AiSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JwtTokenService.class)
    public JwtTokenService jwtTokenService(AuthTokenProperties properties) {
        return new DefaultJwtTokenService(properties);
    }

    @Bean
    @ConditionalOnClass(StringRedisTemplate.class)
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnMissingBean(TokenBlacklistChecker.class)
    public TokenBlacklistChecker redisTokenBlacklistChecker(StringRedisTemplate redisTemplate,
                                                           AuthTokenProperties properties) {
        return new RedisTokenBlacklistChecker(redisTemplate, properties.getBlacklistPrefix());
    }

    @Bean
    @ConditionalOnMissingBean(TokenBlacklistChecker.class)
    public TokenBlacklistChecker tokenBlacklistChecker() {
        return new NoopTokenBlacklistChecker();
    }

    @Bean
    @ConditionalOnMissingBean(TokenResolver.class)
    public TokenResolver tokenResolver(AuthTokenProperties properties) {
        return new TokenResolver(properties);
    }

    @Bean
    @ConditionalOnMissingBean(TokenValidationService.class)
    public TokenValidationService tokenValidationService(JwtTokenService jwtTokenService,
                                                         TokenBlacklistChecker tokenBlacklistChecker) {
        return new TokenValidationService(jwtTokenService, tokenBlacklistChecker);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityUserHeaderWriter.class)
    public SecurityUserHeaderWriter securityUserHeaderWriter(AuthTokenProperties properties) {
        return new SecurityUserHeaderWriter(properties);
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnClass(OncePerRequestFilter.class)
    @ConditionalOnMissingBean(name = "servletTokenValidationFilterRegistration")
    public FilterRegistrationBean<ServletTokenValidationFilter> servletTokenValidationFilterRegistration(
            AuthTokenProperties properties,
            TokenValidationService tokenValidationService,
            TokenResolver tokenResolver,
            SecurityUserHeaderWriter securityUserHeaderWriter,
            ObjectMapper objectMapper) {
        FilterRegistrationBean<ServletTokenValidationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ServletTokenValidationFilter(
                properties,
                tokenValidationService,
                tokenResolver,
                securityUserHeaderWriter,
                objectMapper
        ));
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 20);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnClass(WebFilter.class)
    @ConditionalOnMissingBean(name = "reactiveTokenValidationFilter")
    public WebFilter reactiveTokenValidationFilter(AuthTokenProperties properties,
                                                   TokenValidationService tokenValidationService,
                                                   TokenResolver tokenResolver,
                                                   SecurityUserHeaderWriter securityUserHeaderWriter,
                                                   ObjectMapper objectMapper) {
        return new ReactiveTokenValidationFilter(
                properties,
                tokenValidationService,
                tokenResolver,
                securityUserHeaderWriter,
                objectMapper
        );
    }
}

