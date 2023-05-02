package com.numpyninja.lms.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponseDto {
    private String token;
    private String type = "Bearer";
    private String userId;
    private String username;  // email in LMS Application
    private String email;
    private List<String> roles;

    public JwtResponseDto(String accessToken, String userId, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

}