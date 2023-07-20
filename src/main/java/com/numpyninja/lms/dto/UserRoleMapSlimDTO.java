package com.numpyninja.lms.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.numpyninja.lms.entity.Role;
import com.numpyninja.lms.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleMapSlimDTO {
	
	@NotEmpty(message = "Role Id is mandatory")
	@JsonProperty("roleId")
	private String roleId;
	
	//@JsonProperty("roleName")
	//private String roleName;
	
	@NotEmpty(message = "Role Status is Mandatory")
	@JsonProperty("userRoleStatus")
	@Pattern(regexp = "active|inactive|online|offline|away", flags = Pattern.Flag.CASE_INSENSITIVE,
    message = "User-Role Status should be Active, InActive, Online, Offline,or Away")
	private String userRoleStatus;
}
