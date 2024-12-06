package com.fit.authservice.dtos.request;

import com.fit.authservice.models.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long userId;
    private String email;
    private LocalDate registrationDate;
    private String name;
    private String address;
    private boolean gender;
    private LocalDate dateOfBirth;
    private String phoneNumber;

    public static Customer convertToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setUserId(customerDTO.getUserId());
        customer.setAddress(customerDTO.getAddress());
        customer.setGender(customerDTO.isGender());
        customer.setDateOfBirth(customerDTO.getDateOfBirth());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        return customer;
    }

    public static CustomerDTO convertToDto(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setUserId(customer.getUserId());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setGender(customer.isGender());
        customerDTO.setDateOfBirth(customer.getDateOfBirth());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        return customerDTO;
    }
}
