package com.fit.authservice.dtos.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountRequest {
    private String email;
    private String password;
}
