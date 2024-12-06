package com.fit.notificationservice.dtos.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long userId;
    private String email;
    private LocalDate registrationDate;
    private String name;
    private String address;
    private boolean gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;
}
