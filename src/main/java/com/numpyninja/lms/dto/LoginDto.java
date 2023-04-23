package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginDto {
    @NotBlank(message = "EmailId is mandatory")
    private String userLoginEmailId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotBlank(message = "Password is mandatory")
    private String password;
}
