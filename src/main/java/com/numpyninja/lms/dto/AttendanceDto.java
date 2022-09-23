package com.numpyninja.lms.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {
	
	private Long attId;
	private Long csId;
	private String studentId;
	private String attendance;
	private Timestamp creationTime;
	private Timestamp lastModTime;

}
