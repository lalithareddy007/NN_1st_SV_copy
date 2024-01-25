package com.numpyninja.lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {
	//@com.fasterxml.jackson.annotation.JsonIgnore
	private Long attId;
	private Long csId;
	private String studentId;
	private String attendance;
	private Timestamp creationTime;
	private Timestamp lastModTime;

	@javax.validation.constraints.NotNull(message = "Attendance date must be provided")
	@javax.validation.constraints.PastOrPresent(message = "Attendance date can't be in future")
	private java.time.LocalDate attendanceDate;

}
