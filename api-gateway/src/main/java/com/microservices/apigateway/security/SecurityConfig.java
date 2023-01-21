package com.microservices.apigateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                    .authorizeExchange()
                    .pathMatchers("/actuator/**", "/", "/currency-exchange/**",
                            "/currency-converter/**", "/banking/open")
                    .permitAll()
                .and()
                    .authorizeExchange()
                    .anyExchange()
                    .authenticated()
                .and()
                    .oauth2Login()
        ;
        return http.build();
    }
}