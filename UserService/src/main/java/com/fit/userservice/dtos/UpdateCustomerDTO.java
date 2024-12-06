package com.fit.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCustomerDTO {
    private String name;  // Thêm trường name
    private String address;
    private boolean gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;
}