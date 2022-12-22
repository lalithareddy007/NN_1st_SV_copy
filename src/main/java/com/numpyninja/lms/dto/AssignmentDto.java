package com.numpyninja.lms.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDto {

	private Long assignmentId;

	@NotEmpty(message = "Assignment Name is mandatory")
	private String assignmentName;

	@NotEmpty(message = "Assignment Description is mandatory")
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

	@NotEmpty(message = "Created By is mandatory")
	private String createdBy;

	@NotEmpty(message = "Grader ID is mandatory")
	private String graderId;

}
