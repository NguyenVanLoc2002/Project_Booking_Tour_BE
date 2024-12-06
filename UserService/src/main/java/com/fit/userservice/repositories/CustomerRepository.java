package com.fit.userservice.repositories;

import com.fit.userservice.models.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;


public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    @Query("Select * from customers where user_id = :id")
    Mono<Customer> findByUserId(@Param("id") Long id);

    @Query("UPDATE customers SET address = :address, gender = :gender, date_of_birth = :dateOfBirth, phone_number = :phoneNumber WHERE user_id = :userId")
    Mono<Void> updateCustomer(@Param("userId") Long userId,
                              @Param("address") String address,
                              @Param("gender") boolean gender,
                              @Param("dateOfBirth") LocalDate dateOfBirth,
                              @Param("phoneNumber") String phoneNumber);
}