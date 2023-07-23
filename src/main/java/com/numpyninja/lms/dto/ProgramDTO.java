package com.numpyninja.lms.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.numpyninja.lms.config.ValidateStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Getter
//@Setter

public class ProgramDTO {

		private Long programId;
		private String programName;
		private String programDescription;
		
		//custom annotation to validate status( accepts only "Active" and "Inactive")
		@ValidateStatus
		private String programStatus;
		
		private Timestamp creationTime;
		private Timestamp lastModTime;
}

