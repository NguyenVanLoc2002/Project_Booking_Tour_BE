package com.fit.authservice.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final ReactiveAuthenticationManager authenticationManager;
    private final LogoutService logoutService;

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Sử dụng cùng một cách giải mã
        var secretKey = Keys.hmacShaKeyFor(keyBytes);
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/customers/addCustomer","/auth/verify-account","/auth/login","/auth/get-claims").permitAll()
                        .anyExchange().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())  // Sử dụng JWT cho xác thực
                )
                .authenticationManager(authenticationManager)
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
//                .addFilterAt(jwtAuthFilter,  SecurityWebFiltersOrder.AUTHENTICATION) // Chỉ áp dụng filter sau AUTHENTICATION
                .logout(logout -> logout
                        .logoutUrl("/auth/logout") // URL đăng xuất
                        .logoutHandler(logoutService) // Xử lý logic đăng xuất tùy chỉnh
                        .logoutSuccessHandler((exchange, authentication) -> Mono.empty()) // Không chuyển hướng sau đăng xuất
                )
                .build();
    }
}
