package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.numpyninja.lms.entity.UserLogin;
import lombok.*;

import javax.persistence.Column;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserLoginRoleDTO {
    @JsonProperty("userId")
    private String userId;

    @NotEmpty(message = "User First Name is cannot be null or empty")
    @JsonProperty("userFirstName")
    private String userFirstName;

    @NotEmpty(message = "User Last Name is cannot be null or empty")
    @JsonProperty("userLastName")
    private String userLastName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("userMiddleName")
    private String userMiddleName;

    @NotNull(message = "Phone Number is required")
    @JsonProperty("userPhoneNumber")
    private Long userPhoneNumber;

    @JsonProperty("userLocation")
    private String userLocation;

    @JsonProperty("userTimeZone")
    private String userTimeZone;

    @JsonProperty("userLinkedinUrl")
    private String userLinkedinUrl;

    @JsonProperty("userEduUg")
    private String userEduUg;

    @JsonProperty("userEduPg")
    private String userEduPg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("userComments")
    private String userComments;

    @JsonProperty("userVisaStatus")
    private String userVisaStatus;

    @NotEmpty(message = "User Role Info is mandatory")
    @Valid
    @JsonProperty("userRoleMaps")
    private List<UserRoleMapSlimDTO> userRoleMaps;

    @Valid
    @JsonProperty("userLogin")
    private UserLoginDto userLogin;


}
