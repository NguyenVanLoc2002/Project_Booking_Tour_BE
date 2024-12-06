package com.fit.authservice.repositories;

import com.fit.authservice.models.AuthUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface AuthUserRepository extends ReactiveCrudRepository<AuthUser, Long> {
    Mono<AuthUser> findByEmail(String email);
}
