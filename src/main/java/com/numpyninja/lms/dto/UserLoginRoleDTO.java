package com.numpyninja.lms.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.numpyninja.lms.util.Constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @Pattern(regexp = Constants.REGEX_MIN_2_ALPHABET, 
    	message = "userFirstName " + Constants.MSG_ALPHABET_ONLY_MIN_2)
    private String userFirstName;

    @NotEmpty(message = "User Last Name is cannot be null or empty")
    @JsonProperty("userLastName")
    @Pattern(regexp = Constants.REGEX_MIN_1_ALPHABET, 
	message = "userLastName " + Constants.MSG_ALPHABET_ONLY_MIN_1)
    private String userLastName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("userMiddleName")
    @Pattern(regexp = Constants.REGEX_MIN_1_ALPHABET, 
	message = "userMiddleName " + Constants.MSG_ALPHABET_ONLY_MIN_1)
    private String userMiddleName;

    @NotNull(message = "Phone Number is required")
    @JsonProperty("userPhoneNumber")
    private Long userPhoneNumber;

    @JsonProperty("userLocation")
    private String userLocation;

    @JsonProperty("userTimeZone")
    private String userTimeZone;

    @Pattern(regexp = Constants.REGEX_LINKEDIN_URL, 
    		message = "userLinkedinUrl" + Constants.MSG_INVALID_LINKEDIN_URL)
    @JsonProperty("userLinkedinUrl")
    private String userLinkedinUrl;

    @Pattern(regexp = Constants.REGEX_MIN_2_ALPHA_NUMERIC, 
    		message = "userEduUg " + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
    @JsonProperty("userEduUg")
    private String userEduUg;

    @Pattern(regexp = Constants.REGEX_MIN_2_ALPHA_NUMERIC, 
    		message = "userEduPg" + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
    @JsonProperty("userEduPg")
    private String userEduPg;
    
    @Pattern(regexp = Constants.REGEX_MIN_2_ALPHA_NUMERIC, 
    		message = "userComments " + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("userComments")
    private String userComments;

    @JsonProperty("userVisaStatus")
    private String userVisaStatus;

    @NotEmpty(message = "User Role Info is mandatory")
    @Valid
    @JsonProperty("userRoleMaps")
    private List<UserRoleMapSlimDTO> userRoleMaps;

    //@Valid
    @JsonProperty("userLogin")
    private UserLoginDto userLogin;


}
