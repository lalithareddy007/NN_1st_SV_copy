package com.numpyninja.lms.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.numpyninja.lms.config.ValidateStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BatchDTO {
	private Integer batchId;
	
	@NotBlank (message = "Batch Name is mandatory" )
	private String batchName;
	
	private String batchDescription;
	
	//created custom annotation to validate status( accepts only "Active" and "Inactive")
	@ValidateStatus
	@NotBlank ( message = "Batch status is needed"  )
	private String batchStatus;
	
	@Positive ( message = " No of Classes is needed; It should be a positive number " )
	private int batchNoOfClasses;
	
	@NotNull ( message = " ProgramId field is needed; It should be a positive number " )
	private Long programId;
	
	private String programName;
	
}
