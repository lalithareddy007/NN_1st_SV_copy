package com.numpyninja.lms.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

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
	
	@NotBlank (message = "Batch Name is mandatory" )
	@Pattern(regexp = "(^[a-zA-Z][a-zA-Z0-9 ]+$)", message = "Batch Name can contain only alphabets and numbers")
	@Length(min = 4, max = 25, message = "Program Name must be of min length 4 and max length 25")
	private String batchName;

	@Pattern (regexp="^[a-z0-9][a-z0-9_ ]*(?:-[a-z0-9]+)*$", message = "Batch Desc can contain only alphabets and numbers")
	@Length(min = 4, max = 25, message = "Batch Description must be of min length 4 and max length 25")
	private String batchDescription;
	
	@NotBlank ( message = "Batch status is needed"  )
	private String batchStatus;
	
	@Positive ( message = " No of Classes is needed; It should be a positive number " )
	private int batchNoOfClasses;
	
	@NotNull ( message = " ProgramId field is needed; It should be a positive number " )
	@Positive ( message = " ProgramId should be a positive number " )
	private Long programId;

	@Pattern(regexp = "(^[a-zA-Z][a-zA-Z0-9 ]+$)", message = "Program Name can contain only alphabets and numbers")
	@Length(min = 4, max = 25, message = "Program Name must be of min length 4 and max length 25")
	private String programName;
	
}
