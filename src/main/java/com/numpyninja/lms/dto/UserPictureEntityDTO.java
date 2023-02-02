package com.numpyninja.lms.dto;

import java.math.BigInteger;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.numpyninja.lms.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPictureEntityDTO {
	
	
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
