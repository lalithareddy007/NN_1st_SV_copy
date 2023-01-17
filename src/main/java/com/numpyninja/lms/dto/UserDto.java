package com.numpyninja.lms.dto;

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
	
	@JsonProperty("userFirstName")
	private String userFirstName;
	
	@JsonProperty("userLastName")
	private String userLastName;
	
	@JsonProperty("userMiddleName")
	private String userMiddleName;
	
	@JsonProperty("userPhoneNumber")
	private long userPhoneNumber;
	
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
	
	@JsonProperty("userComments")
	private String userComments;
	
	@JsonProperty("userVisaStatus")
	private String userVisaStatus;


	
}
