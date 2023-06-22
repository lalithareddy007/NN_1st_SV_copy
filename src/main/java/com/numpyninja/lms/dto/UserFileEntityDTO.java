package com.numpyninja.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFileEntityDTO {
	
	
	//Long userFileId;
	private Long userFileId;
	
	//@JsonIgnore
	//@JsonProperty("userFileType")
	
    private String userFileType; 
	
	//@JsonIgnore
	//@JsonProperty("userId")
	private String userId;
	
	//@JsonIgnore
	//@JsonProperty("userFilePath")
    private String userFilePath;
	
}
