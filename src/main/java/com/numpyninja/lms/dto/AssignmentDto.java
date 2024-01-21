package com.numpyninja.lms.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.numpyninja.lms.util.Constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDto {

	private Long assignmentId;

	@NotEmpty(message = "Assignment Name is mandatory")
	@Pattern(regexp=Constants.REGEX_MIN_2_ALPHA_NUMERIC, message= "assignmentName " + Constants.MSG_ALPHANUMERIC_ONLY_MIN_2)
	@Size(min = 4, max = 25, message = "assignmentName must contain minimum of 4 and maximum of 25 characters")
	private String assignmentName;

	@NotEmpty(message = "Assignment Description is mandatory")
	@Pattern(regexp=Constants.REGEX_DESC_ALPHA_NUMERIC_SPCL, message= "assignmentDescription " + Constants.MSG_DESC_ALPHA_NUMERIC_SPCL)
	@Size(min = 4, max = 25, message = "Assignment Description must contain minimum of 4 and maximum of 25 characters")
	private String assignmentDescription;

	private String comments;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Temporal(TemporalType.DATE)
	@NotNull(message = "Due Date is mandatory")
	private Date dueDate;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String pathAttachment1;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String pathAttachment2;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String pathAttachment3;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String pathAttachment4;
													
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String pathAttachment5;

	//Batch entity details
	@NotNull(message = "Batch ID is mandatory")
	private Integer batchId;

	@NotNull(message = "Class ID is mandatory")
	private Long  csId;

	@NotEmpty(message = "Created By is mandatory")
	private String createdBy;

	@NotEmpty(message = "Grader ID is mandatory")
	private String graderId;

}
