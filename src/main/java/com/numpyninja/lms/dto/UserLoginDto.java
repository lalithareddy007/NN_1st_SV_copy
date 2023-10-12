package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.numpyninja.lms.config.ValidateStatus;

import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(value = {"roleIds"}, allowGetters = true)
public class UserLoginDto {
    @NotBlank(message = "UserLoginEmail is mandatory")
    private String userLoginEmail;

    private String password;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String loginStatus;

  //created custom annotation to validate status( accepts only "Active" and "Inactive")
    @ValidateStatus
    //@JsonIgnore
    private String status;

    private List<String> roleIds;

}
