package com.fit.apigateway.services;

import java.util.List;

import com.fit.apigateway.dtos.response.ApiResponse;
import com.fit.apigateway.dtos.response.ClaimsResponse;
import com.fit.apigateway.repositories.AuthenticateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@Slf4j
public class AuthenticateService {
    @Autowired
    private AuthenticateClient authenticateClient;
    public Mono<ApiResponse<ClaimsResponse>> getClaims(String authorization) {
        log.info("Calling AuthService to get claims with header: {}", authorization);
        return authenticateClient.getClaims(authorization)
                .doOnSuccess(claimsResponse -> log.info("Received claims: {}", claimsResponse))
                .doOnError(error -> log.error("Error calling AuthService: {}", error.getMessage()));
    }
}
