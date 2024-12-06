package com.fit.authservice.dtos;

import com.fit.authservice.enums.Role;
import com.fit.authservice.models.AuthUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserDTO {
    private Long id;
    private String email;
    private String password;
    private Role role;

    public static AuthUser convertToEntity(AuthUserDTO authUserDTO) {
        AuthUser authUser = new AuthUser();
        authUser.setId(authUserDTO.getId());
        authUser.setEmail(authUserDTO.getEmail());
        authUser.setPassword(authUserDTO.getPassword());
        authUser.setRole(authUserDTO.getRole());
        return authUser;
    }

    public static AuthUserDTO convertToDto(AuthUser authUser) {
        AuthUserDTO authUserDTO = new AuthUserDTO();
        authUserDTO.setId(authUser.getId());
        authUserDTO.setEmail(authUser.getEmail());
        authUserDTO.setPassword(authUser.getPassword());
        authUserDTO.setRole(authUser.getRole());
        return authUserDTO;
    }
}
