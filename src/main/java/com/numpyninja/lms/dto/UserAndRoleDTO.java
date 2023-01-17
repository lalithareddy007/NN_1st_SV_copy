package com.numpyninja.lms.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.numpyninja.lms.entity.UserRoleMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAndRoleDTO {
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
	
	@JsonProperty("userRoleMaps")
	private List<UserRoleMapSlimDTO> userRoleMaps;
	
	//@JsonProperty("UserRoleId")
	//private String role_id;
	
	//@JsonProperty("UserRoleName")
	//private String roleName;
	
	//@JsonProperty("UserRole")
	//private UserRoleMap role;

	//@JsonProperty("userRoleStatus")
	//private String userRoleStatus;
	
	
}
