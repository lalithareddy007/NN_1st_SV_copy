package com.numpyninja.lms.dto;

import java.sql.Timestamp;

import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.numpyninja.lms.config.ValidateStatus;
import com.numpyninja.lms.util.Constants;

import com.numpyninja.lms.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@Getter
//@Setter

public class ProgramDTO {

		private Long programId;

	    @Pattern(regexp = "(^[a-zA-Z][a-zA-Z0-9 ]+$)", message = "Program Name can contain only alphabets and numbers")
	    @Length(min = 4, max = 25, message = "Program Name must be of min length 4 and max length 25")

		private String programName;


		@Pattern (regexp="^[a-z0-9][a-z0-9_ ]*(?:-[a-z0-9]+)*$", message = "Program Desc can contain only alphabets and numbers")
		@Length(min = 4, max = 25, message = "Program Description must be of min length 4 and max length 25")
	   	private String programDescription;

	    //created custom annotation to validate status(accepts only active and inactive)
	    @ValidateStatus
		private String programStatus;


		private Timestamp creationTime;
		private Timestamp lastModTime;

}

