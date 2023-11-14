package com.numpyninja.lms.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
	
	/*
	 * @NotEmpty(message="Role Id is mandatory")
	 * 
	 * @JsonProperty("roleId") private String roleId;
	 */
    
	@NotNull
    @NotEmpty(message = "User Role Info is mandatory")
    @Valid
    @JsonProperty("userRoleList")
    private List<String> userRoleList;
    
    

}
