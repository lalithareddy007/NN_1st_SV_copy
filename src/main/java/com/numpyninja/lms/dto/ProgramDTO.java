package com.numpyninja.lms.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.numpyninja.lms.config.ValidateStatus;

import com.numpyninja.lms.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Getter
//@Setter

public class ProgramDTO {

		private Long programId;
		private String programName;
	   	private String programDescription;
		
		//created custom annotation to validate status(accepts only active and inactive)
		@ValidateStatus
		private String programStatus;
		
		private Timestamp creationTime;
		private Timestamp lastModTime;
}

