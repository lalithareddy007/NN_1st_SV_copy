package com.numpyninja.lms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_lms_attendance")
public class Attendance {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="att_id")
	private Long attId;
	
	@ManyToOne (fetch=FetchType.LAZY)     
    @JoinColumn (name="cs_id", nullable=false)  
    private Class objClass; 
	
	
	@ManyToOne (fetch=FetchType.LAZY)     
    @JoinColumn (name="student_id", nullable=false)  
    private User user; 
	
	@NotEmpty
	@Column(name="attendance")
	private String attendance;

	@Column(name="creation_time")
	private Timestamp creationTime;
	
	@Column(name="last_mod_time")
	private Timestamp lastModTime;

	@Column(name="attendence_date")
	private java.time.LocalDate attendanceDate;

	
}

