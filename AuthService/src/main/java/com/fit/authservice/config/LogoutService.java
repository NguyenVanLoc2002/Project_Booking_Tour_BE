package com.fit.authservice.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class LogoutService implements ServerLogoutHandler {


    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        String authHeader = exchange.getExchange().getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new RuntimeException("Invalid token format")); // Xử lý lỗi rõ ràng
        }

        return Mono.fromRunnable(() -> {
            ReactiveSecurityContextHolder.clearContext();
            exchange.getExchange().getResponse().setStatusCode(HttpStatus.OK); // Đặt trạng thái 200 OK sau khi đăng xuất
        });
    }

}
