package com.numpyninja.lms.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private String userId;

    private String email;
    private List<String> roles;
    private String status;

    public JwtResponseDto(String accessToken, String userId, String email, List<String> roles) {
        this.token = accessToken;
        this.userId = userId;
        this.email = email;
        this.roles = roles;
    }

}