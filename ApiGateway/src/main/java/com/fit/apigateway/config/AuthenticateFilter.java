package com.fit.apigateway.config;

import com.fit.apigateway.enums.ErrorCode;
import com.fit.apigateway.enums.Role;
import com.fit.apigateway.exceptions.AppException;
import com.fit.apigateway.services.AuthenticateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@Component
@Slf4j
public class AuthenticateFilter extends AbstractGatewayFilterFactory<AuthenticateFilter.Config> {

    @Autowired
    private RouterValidator routerValidator;

    @Autowired
    private AuthenticateService authenticateService;

    public AuthenticateFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("API load balance: {}", exchange.getRequest().getURI().getPath());
            if (!routerValidator.isSecured.test(exchange.getRequest())) {
                log.info("Open Endpoint: {}", exchange.getRequest().getURI().getPath());
                return chain.filter(exchange);
            } else if (routerValidator.isInternal.test(exchange.getRequest())) {
                log.info("Internal Endpoint");
                log.info("Request to : {}", exchange.getRequest().getURI().getPath());
                return Mono.error(new AppException(ErrorCode.UNAUTHORIZED));
            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);


            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                return Mono.error(new AppException(ErrorCode.UNAUTHENTICATED));
            }
            log.info("authorizationHeader 1: {}", authorizationHeader);
            return authenticateService.getClaims(authorizationHeader)
                    .flatMap(claimsResponse -> {
                        // Kiểm tra vai trò người dùng (admin, student, ...).
                        log.info("Role: {}", claimsResponse.getResult().getRole());
                        boolean isAdmin = claimsResponse.getResult().getRole().equals(Role.ADMIN);
                        boolean isUser = claimsResponse.getResult().getRole().equals(Role.USER);

                        // Kiểm tra quyền truy cập dựa trên vai trò
                        Predicate<ServerHttpRequest> endpointSelector = null;
                        if (isUser) {
                            endpointSelector = routerValidator.customerEndpoints;
                        } else if (isAdmin) {
                            endpointSelector = routerValidator.adminEndpoints;
                        }

                        if (endpointSelector != null && !endpointSelector.test(exchange.getRequest())) {
                            return Mono.error(new AppException(ErrorCode.UNAUTHORIZED));
                        }

                        return chain.filter(exchange);
                    })
                    .onErrorResume(throwable -> {
                        log.error("Authentication error: {}", throwable.getMessage());
                        return Mono.error(new AppException(ErrorCode.UNAUTHENTICATED));
                    });
        };
    }

    public static class Config {
    }
}
