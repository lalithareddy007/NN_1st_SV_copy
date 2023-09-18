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

		@Pattern(regexp = Constants.REGEX_MIN_2_ALPHA_NUMERIC,
			message = "programName " + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
	    private String programName;

		@Pattern(regexp = Constants.REGEX_MIN_2_ALPHA_NUMERIC,
			message = "programDescription " + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
	   	private String programDescription;

	    //created custom annotation to validate status(accepts only active and inactive)
	    @ValidateStatus
		private String programStatus;

		private Timestamp creationTime;
		private Timestamp lastModTime;

}

