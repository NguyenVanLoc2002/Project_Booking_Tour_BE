package com.fit.authservice.config;

import com.fit.authservice.models.AuthUser;
import com.fit.authservice.repositories.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuthConfig {
    @Autowired
    private AuthUserRepository authUserRepository;

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return username -> authUserRepository.findByEmail(username)
                .doOnNext(user -> log.info("User found: " + user.getEmail())) // Log khi tìm thấy user
                .map(this::convertToUserDetails)
                .doOnError(error -> log.error("Error occurred while finding user: ", error)) // Log lỗi
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found for email: " + username)));
    }

    private UserDetails convertToUserDetails(AuthUser authUser) {
        // Lấy quyền hạn từ AuthUser (nếu có)
        Collection<? extends GrantedAuthority> authorities = List.of(authUser.getRole());

        // Tạo và trả về đối tượng UserDetails
        return new User(
                authUser.getEmail(),
                authUser.getPassword(),
                authorities
        );
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        authManager.setPasswordEncoder(passwordEncoder);  // Đặt PasswordEncoder
        return authManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
