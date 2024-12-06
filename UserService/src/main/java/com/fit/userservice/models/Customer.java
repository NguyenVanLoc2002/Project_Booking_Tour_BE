package com.fit.userservice.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "customers")
public class Customer {
    @Column("user_id")
    private Long userId;
    private String address;
    private boolean gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;
}