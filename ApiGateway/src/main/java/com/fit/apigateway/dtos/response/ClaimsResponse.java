package com.fit.apigateway.dtos.response;

import com.fit.apigateway.enums.Role;
import lombok.*;

import java.util.Set;

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
