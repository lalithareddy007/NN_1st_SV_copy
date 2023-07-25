package com.numpyninja.lms.dto;

import javax.validation.constraints.NotEmpty;

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
	private String userRoleStatus;
}
