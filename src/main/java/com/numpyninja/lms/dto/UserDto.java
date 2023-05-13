package com.numpyninja.lms.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	@JsonProperty("userId")
	private String userId;
	
	@NotEmpty(message = "User First Name is mandatory")
	@JsonProperty("userFirstName")
	private String userFirstName;
	
	@NotEmpty(message = "User Last Name is mandatory")
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

	@JsonProperty("userLoginEmail")
	private String userLoginEmail;

}
