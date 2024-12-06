package com.fit.userservice.dtos;

import com.fit.userservice.enums.AdminPermission;
import com.fit.userservice.models.Admin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    private Long userId;
    private String email;
    private LocalDate registrationDate;
    private AdminPermission permission;


    public static Admin convertToEntity(AdminDTO adminDTO) {
        Admin admin = new Admin();
        admin.setUserId(adminDTO.getUserId());
        admin.setPermission(adminDTO.getPermission());
        return admin;
    }

    public static AdminDTO convertToDto(Admin admin) {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setUserId(admin.getUserId());
        adminDTO.setPermission(admin.getPermission());
        return adminDTO;
    }
}
