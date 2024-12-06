package com.fit.apigateway.repositories;

import com.fit.apigateway.dtos.response.ApiResponse;
import com.fit.apigateway.dtos.response.ClaimsResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;


public interface AuthenticateClient {
    @GetExchange(url = "/auth/get-claims", accept = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<ClaimsResponse>> getClaims(@RequestHeader("Authorization") String authorization);
}
