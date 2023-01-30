package com.numpyninja.lms.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.numpyninja.lms.config.UserIDGenerator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TBL_LMS_USER")
public class User {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tbl_lms_user_user_id_seq")
    @GenericGenerator(name = "tbl_lms_user_user_id_seq", strategy = "com.numpyninja.lms.config.UserIDGenerator",
            parameters = {
            @Parameter(name = UserIDGenerator.INCREMENT_PARAM, value = "1"),
            @Parameter(name = UserIDGenerator.VALUE_PREFIX_PARAMETER, value = "U"),
            @Parameter(name = UserIDGenerator.NUMBER_FORMAT_PARAMETER, value = "%02d") })
	private String userId;
	
	@Column
	private String userFirstName;
	
	@Column
	private String userLastName;
	
	@Column
	private String userMiddleName;
	
	@Column
	private Long userPhoneNumber;
	
	@Column
	private String userLocation;
	
	@Column
	private String userTimeZone;
	
	@Column
	private String userLinkedinUrl;
	
	@Column
	private String userEduUg;
	
	@Column
	private String userEduPg;
	
	@Column
	private String userComments;
	
	@Column
	private String userVisaStatus;
	
	@Column
	@JsonIgnore
	private Timestamp creationTime;
	
	@Column
	@JsonIgnore
	private Timestamp lastModTime;

		
	
}
