package com.numpyninja.lms.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ForgotPasswordResponseDto {

	
	private String token;
    private String type = "Bearer";
    private String userLoginEmailId;
    private String status;
  

    public ForgotPasswordResponseDto(String accessToken,String email) {
        this.token = accessToken;
        this.userLoginEmailId = email;
        this.status = status;
       
    }

}
