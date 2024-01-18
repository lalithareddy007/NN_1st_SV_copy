package com.numpyninja.lms.dto;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClassDto {
private Long csId;
private Integer batchId;
private Integer classNo;
@DateTimeFormat(pattern = "dd/MM/yyyy h:mm a")
private Date classDate;
private String classTopic;
private String classStatus;
private String classStaffId;
private String classDescription;
private String classComments;
private String classNotes;
private String classRecordingPath;
//private String createdby;

//private Timestamp creationTime;
//private Timestamp lastModTime;
}
