package com.fit.authservice.controllers;

import com.fit.authservice.dtos.AuthUserDTO;
import com.fit.authservice.dtos.request.AccountRequest;
import com.fit.authservice.dtos.request.ChangePasswordRequest;
import com.fit.authservice.dtos.request.CustomerDTO;
import com.fit.authservice.dtos.response.ApiResponse;
import com.fit.authservice.dtos.response.ClaimsResponse;
import com.fit.authservice.dtos.response.LoginResponse;
import com.fit.authservice.enums.Role;
import com.fit.authservice.services.AuthService;
import com.fit.authservice.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/verify-account")
    public Mono<ResponseEntity<Object>> verifyAccount(
            @RequestParam("token") String token,
            @RequestParam("redirectUrl") String redirectUrl) {

        log.info("Received request to verify account with token: {}, redirectUrl: {}", token, redirectUrl);

        return Mono.fromCallable(() -> jwtUtils.extractAllClaims(token))
                .flatMap(claims -> {
                    log.info("Extracted claims: {}", claims);
                    String email = claims.get("email", String.class);
                    String name = claims.get("name", String.class);
                    Boolean gender = claims.get("gender", Boolean.class);
                    LocalDate dateOfBirth = LocalDate.parse(claims.get("dateOfBirth", String.class));

                    // Validate email
                    if (email == null) {
                        log.error("Email not found in claims");
                        URI errorUri = URI.create(redirectUrl + "?error=invalid_token");
                        return Mono.just(ResponseEntity.status(HttpStatus.FOUND).location(errorUri).build());
                    }

                    // Prepare DTOs
                    CustomerDTO customerDTO = new CustomerDTO();
                    customerDTO.setEmail(email);
                    customerDTO.setName(name);
                    customerDTO.setGender(gender != null ? gender : false); // Default to false if null
                    customerDTO.setDateOfBirth(dateOfBirth);

                    AuthUserDTO authUserDTO = new AuthUserDTO();
                    authUserDTO.setEmail(email);
                    authUserDTO.setPassword("123456"); // Temporary password
                    authUserDTO.setRole(Role.USER);

                    log.info("Prepared CustomerDTO: {}", customerDTO);
                    log.info("Prepared AuthUserDTO: {}", authUserDTO);

                    // Save AuthUser and Customer
                    return authService.createAuthUser(authUserDTO)
                            .flatMap(authSaved -> authService.registerUser(customerDTO)
                                    .then(Mono.fromCallable(() -> {
                                        String redirectWithParams = redirectUrl + "?email=" + email + "&password=123456";
                                        URI successUri = URI.create(redirectWithParams);
                                        log.info("Account successfully verified. Redirecting to: {}", successUri);
                                        return ResponseEntity.status(HttpStatus.FOUND).location(successUri).build();
                                    })))
                            .switchIfEmpty(Mono.fromCallable(() -> {
                                log.error("AuthUser creation failed for email: {}", email);
                                URI errorUri = URI.create(redirectUrl + "?error=user_creation_failed");
                                return ResponseEntity.status(HttpStatus.FOUND).location(errorUri).build();
                            }));
                })
                .onErrorResume(ex -> {
                    log.error("Error during account verification. Token: {}, Error: {}", token, ex.getMessage(), ex);
                    URI errorUri = URI.create(redirectUrl + "?error=unexpected_error");
                    return Mono.just(ResponseEntity.status(HttpStatus.FOUND).location(errorUri).build());
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody AccountRequest accountRequest) {
        return authService.login(accountRequest)
                .map(loginResponse -> ResponseEntity.ok().body(loginResponse))
                .onErrorResume(e -> {
                    log.error("Error during login", e);
                    LoginResponse defaultResponse = null;
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(defaultResponse));
                });
    }

    @GetMapping("/get-claims")
    public Mono<ApiResponse<ClaimsResponse>> getClaims(@RequestHeader("Authorization") String authorization) {
        log.info("Get claims");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return Mono.just(ApiResponse.<ClaimsResponse>builder()
                    .code(HttpStatus.UNAUTHORIZED.value())
                    .message("Invalid authorization header.")
                    .build());
        }

        String token = authorization.substring(7);

        return Mono.just(token)
                .flatMap(t -> {
                    // Validate and extract claims from the token
                    String username = jwtUtils.extractUsername(t);
                    Claims claims = jwtUtils.extractAllClaims(t);

                    if (username == null) {
                        return Mono.just(ApiResponse.<ClaimsResponse>builder()
                                .code(HttpStatus.UNAUTHORIZED.value())
                                .message("Invalid token.")
                                .build());
                    }

                    // Extract the role from claims
                    String roleClaim = claims.get("role", String.class);
                    Role role = Role.valueOf(roleClaim); // Convert the string to the Enum

                    // Construct the response object
                    ClaimsResponse claimsResponse = new ClaimsResponse(username, role);
                    log.info("claimsResponse: {}", claimsResponse);
                    return Mono.just(ApiResponse.<ClaimsResponse>builder()
                            .code(HttpStatus.OK.value())
                            .result(claimsResponse)
                            .build());
                })
                .onErrorResume(e -> {
                    log.error("Error while extracting claims: {}", e.getMessage());
                    return Mono.just(ApiResponse.<ClaimsResponse>builder()
                            .code(HttpStatus.UNAUTHORIZED.value())
                            .message("Token validation failed.")
                            .build());
                });
    }

    @PutMapping("/change-password")
    public Mono<ResponseEntity<String>> changePassword(@RequestBody ChangePasswordRequest request) {
        return authService.changePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword())
                .map(result -> ResponseEntity.ok("Mật khẩu đã được thay đổi thành công"))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }
}
