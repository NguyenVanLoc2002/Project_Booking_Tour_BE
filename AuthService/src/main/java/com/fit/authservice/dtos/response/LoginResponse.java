package com.fit.authservice.dtos.response;

import com.fit.authservice.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoginResponse {
    private String email;
    private Role role;
    private String token;
}
