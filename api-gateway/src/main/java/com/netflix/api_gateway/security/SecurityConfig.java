//package com.netflix.api_gateway.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        System.out.println("✅ SecurityConfig LOADED");
//
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//
//                .authorizeExchange(exchanges -> exchanges
//                        .pathMatchers("/auth/**", "/actuator/**").permitAll()
//                        .anyExchange().authenticated()
//                )
//
//                .build();
//    }
//}
