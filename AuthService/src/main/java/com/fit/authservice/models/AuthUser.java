package com.fit.authservice.models;

import com.fit.authservice.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_users")
public class AuthUser {
    @Id
    @Column("auth_id")
    private Long id;
    private String email;
    private String password;
    private Role role;
    private boolean isActive;
}
