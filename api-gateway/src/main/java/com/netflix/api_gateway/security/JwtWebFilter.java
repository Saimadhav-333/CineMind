package com.netflix.api_gateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    public JwtWebFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // Allow auth endpoint without token
        if (exchange.getRequest().getPath().toString().startsWith("/auth")) {
            return chain.filter(exchange);
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);

        // ✅ Validate token & extract userId
        String userId = jwtUtil.validateAndExtractUserId(token);
        System.out.println("✅ JWT FILTER HIT — userId = " + userId);

        // ✅ Add X-User-Id header
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(
                        exchange.getRequest()
                                .mutate()
                                .header("X-User-Id", userId)
                                .build()
                )
                .build();

        return chain.filter(mutatedExchange);
    }
}
