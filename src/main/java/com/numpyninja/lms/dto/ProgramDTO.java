package com.numpyninja.lms.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
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
		private String programStatus;
		
		private Timestamp creationTime;
		private Timestamp lastModTime;
}

