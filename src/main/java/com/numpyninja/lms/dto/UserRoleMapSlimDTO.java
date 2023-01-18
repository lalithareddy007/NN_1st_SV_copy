package com.numpyninja.lms.dto;

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
	
	//@JsonProperty("userId")
	//private String userId;
	
	@JsonProperty("roleId")
	private String roleId;
	//private Role role;
	
	//@JsonProperty("roleName")
	//private String roleName;
	
	@JsonProperty("userRoleStatus")
	private String userRoleStatus;
}
