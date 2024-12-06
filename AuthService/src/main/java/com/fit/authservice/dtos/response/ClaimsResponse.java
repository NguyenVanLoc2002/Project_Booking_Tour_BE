package com.fit.authservice.dtos.response;


import com.fit.authservice.enums.Role;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ClaimsResponse {
    String sub;
    Role role;
}
