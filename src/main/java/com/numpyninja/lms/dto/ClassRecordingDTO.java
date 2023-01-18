package com.numpyninja.lms.dto;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Id;

import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassRecordingDTO{

	
	//private Integer recordid;
	@Id
	private Long csId;
	
	private String classRecordingPath;
	// private Integer batchid;
	 //private Integer batchid;
	 
}
