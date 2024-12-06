package com.fit.authservice.config;

import com.fit.authservice.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtUtils jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Lấy URL path hiện tại
        String path = exchange.getRequest().getPath().value();

        // Bỏ qua JWT filter cho các đường dẫn không cần xác thực
        if (path.equals("/api/v1/auth/login") || path.equals("/api/v1/customers/addCustomer") || path.equals("/api/v1/auth/verify-account")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Authorization header");
            return chain.filter(exchange);
        }

        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);
        log.info("Extracted email: " + userEmail);

        if (userEmail == null) {
            return chain.filter(exchange);
        }

        return userDetailsService.findByUsername(userEmail)
                .flatMap(userDetails -> {
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());

                        // Thiết lập SecurityContext mới
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                    } else {
                        log.info("Invalid JWT token for user: " + userEmail);
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return chain.filter(exchange);  // Nếu token không hợp lệ
                    }

                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("User not found for email: " + userEmail);
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }));
    }

}
