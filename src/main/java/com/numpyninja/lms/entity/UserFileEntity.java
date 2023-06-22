package com.numpyninja.lms.entity;

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

public class UserFileEntity {

	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	
	@Column(name = "user_file_id")
	private Long userFileId;
	//nextval('tbl_lms_user_files_user_file_id_seq'::regclass)
	
	@Column(name = "user_file_type")
	private String userFileType; 
	
	
//	@GenericGenerator(name = "tbl_lms_user_files_user_file_id_seq", strategy = "com.numpyninja.lms.config.UserIDGenerator",
//		    parameters = {
//		    @Parameter(name = UserIDGenerator.INCREMENT_PARAM, value = "1"),
//		    @Parameter(name = UserIDGenerator.VALUE_PREFIX_PARAMETER, value = "U"),
//		    @Parameter(name = UserIDGenerator.NUMBER_FORMAT_PARAMETER, value = "%02d") })
		//private String userId;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "user_id" , nullable = false)
     private User user;
   
	@Column(name = "user_file_path")
	private String userFilePath;
	
	
	
	
	//private Timestamp creationTime;
	
	

	//private Timestamp lastModTime;
	
	
}
