package com.numpyninja.lms.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity

@Getter
@Setter
@NoArgsConstructor
@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tbl_lms_user_files")

public class UserPictureEntity {

	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	@Column(name = "user_file_id")
	Integer userFileId;

	
	@Column(name = "user_file_type")
	private String userFileType; 
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "user_id" , nullable = false)
     private User user;
   
	@Column(name = "user_file_path")
	private String userFilePath;
	
	@Transient
	@Lob
	private byte[] userFileData; 
	
	
	//private Timestamp creationTime;
	
	

	//private Timestamp lastModTime;
	
	
}
