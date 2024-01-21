package com.numpyninja.lms.entity;


import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_lms_assignments")
public class Assignment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="a_id")
	private Long assignmentId;
	
	@Column(name="a_name")
	private String assignmentName;

	@Column(name="a_description")
	private String assignmentDescription;

	@Column(name="a_comments")
	private String comments;
	
	@Column(name="a_due_date")
	private Date dueDate;
	
	@Column(name="a_path_attach1")
	private String pathAttachment1;
	
	@Column(name="a_path_attach2")
	private String pathAttachment2;

	@Column(name="a_path_attach3")
	private String pathAttachment3;
	
	@Column(name="a_path_attach4")
	private String pathAttachment4;

	@Column(name="a_path_attach5")
	private String pathAttachment5;

	@ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn (name="a_batch_id", nullable=false)  
    private Batch batch;

	@ManyToOne (fetch = FetchType.LAZY)
	@JoinColumn(name="a_cs_id", nullable=false)
	private Class aclass;

	@ManyToOne (fetch=FetchType.LAZY)
    @JoinColumn (name="a_created_by", nullable=false)
    private User user;

	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn (name="a_grader_id", nullable=false)
	private User user1;
	
	@Column(name="creation_time")
	private Timestamp creationTime;
	
	@Column(name="last_mod_time")
	private Timestamp lastModTime;
	
}
