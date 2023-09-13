package com.numpyninja.lms.dto;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleIdDTO {
	
	@NotEmpty(message="Role Id is mandatory")
	@JsonProperty("roleId")
	private String roleId;

}
