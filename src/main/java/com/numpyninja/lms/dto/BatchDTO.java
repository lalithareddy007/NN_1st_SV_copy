package com.numpyninja.lms.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

import com.numpyninja.lms.config.ValidateStatus;

import com.numpyninja.lms.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BatchDTO {
	private Integer batchId;

	@Pattern(regexp = Constants.REGEX_MIN_2_ALPHA_NUMERIC,
			message = "batchName " + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
	private String batchName;

	@Pattern(regexp = Constants.REGEX_MIN_2_ALPHA_NUMERIC,
			message = "batchDescription " + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
	private String batchDescription;
	

	//created custom annotation to validate status( accepts only "Active" and "Inactive")
	@ValidateStatus

	@NotBlank ( message = "Batch status is needed"  )
	private String batchStatus;
	
	@Positive ( message = " No of Classes is needed; It should be a positive number " )
	private int batchNoOfClasses;
	
	@NotNull ( message = " ProgramId field is needed; It should be a positive number " )
	@Positive ( message = " ProgramId should be a positive number " )
	private Long programId;

	@Pattern(regexp = Constants.REGEX_MIN_2_ALPHA_NUMERIC,
			message = "programName " + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
	private String programName;
	
}
