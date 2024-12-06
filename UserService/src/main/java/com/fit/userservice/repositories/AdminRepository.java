package com.fit.userservice.repositories;

import com.fit.userservice.models.Admin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;


public interface AdminRepository extends ReactiveCrudRepository<Admin, String> {
}