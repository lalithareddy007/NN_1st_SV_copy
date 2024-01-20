package com.numpyninja.lms.entity;

import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity 
@Table ( name = "tbl_lms_userrole_map" ) 
public class UserRoleMap {
	@Id  
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_id_generator")
	@SequenceGenerator(name = "user_role_id_generator", sequenceName = "tbl_lms_userrole_map_user_role_id_seq", allocationSize = 1)
	@Column( name ="user_role_id")
	private Long userRoleId;
	
	@ManyToOne (   fetch = FetchType.LAZY )
    @JoinColumn ( name = "user_id", nullable = false )
	private User user;
	
	@ManyToOne (  fetch = FetchType.LAZY )  
    @JoinColumn ( name = "role_id", nullable = false )
	private Role role;
	
	/*@ManyToMany   //  defualt fetch is FetchType.LAZY ; so we dont need to specify expilcitly
	@JoinTable(name="tbl_lms_userbatch_map",
               joinColumns={@JoinColumn(name="user_role_id")},
               inverseJoinColumns={@JoinColumn(name="batch_id")})
	private Set<Batch> batches;*/
	
	@Column( name ="user_role_status")
	private String userRoleStatus;
	
	@JsonIgnore
	@Column( name ="creation_time")
	private Timestamp creationTime;
	
	@JsonIgnore
	@Column( name ="last_mod_time")
	private Timestamp lastModTime;
	public String getUserId() {
		return user != null ? user.getUserId() : null;
	}
}

