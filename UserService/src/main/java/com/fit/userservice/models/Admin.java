package com.fit.userservice.models;

import com.fit.userservice.enums.AdminPermission;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "admins")
public class Admin {
    @Column("user_id")
    private Long userId;
    private AdminPermission permission;
}
