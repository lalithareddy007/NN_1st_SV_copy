package com.numpyninja.lms.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class UserCountByStatusDTO {
	
	String status;
	Long count;
	public UserCountByStatusDTO(String status, Long count) {
		super();
		this.status = status;
		this.count = count;
	}
	

}
