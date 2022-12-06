package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserLoginDto {
    @NotBlank(message = "Username is mandatory")
    private String username;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotBlank(message = "Password is mandatory")
    private String password;

    @JsonIgnore
    private String status;

    private List<String> roleIds;
}
