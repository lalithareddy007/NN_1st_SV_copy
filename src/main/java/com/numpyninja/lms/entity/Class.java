package com.numpyninja.lms.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tbl_lms_class_sch")
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_id_generator")
    @SequenceGenerator(name = "class_id_generator", sequenceName = "tbl_lms_class_sch_cs_id_seq", allocationSize = 1)
    @Column(name="cs_id")
    private Long csId;
    
   // @Column(name="batch_id")
    //private Integer batchId;
    
    @ManyToOne//(fetch= FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="batch_id", nullable=false)//,insertable=false, updatable=false,referencedColumnName = "batch_id", unique = true)
	//@JsonIgnore
	@javax.persistence.Embedded
	@AttributeOverride( name = "batchId", column = @Column(name = "batch_id"))
	private Batch batchInClass;
    
    @Column(name="class_no")
    private Integer classNo;
    
    @DateTimeFormat(pattern = "dd/MM/yyyy h:mm a")
    @Column(name="class_date")
    private Date classDate;
    
    @Column(name="class_topic")
    private String classTopic;
    @Column(name="class_status")
    private String classStatus;
    //@Column(name="class_staff_id")
    //private String classStaffId;
    //present one working 
   @ManyToOne//(fetch= FetchType.LAZY,cascade = CascadeType.ALL)
	@JoinColumn(name="class_staff_id", nullable=false)//,insertable=false, updatable=false,referencedColumnName = "program_id", unique = true)
	//@JsonIgnore
	@javax.persistence.Embedded
	@AttributeOverride( name = "userId", column = @Column(name = "class_staff_id"))	
	//@AttributeOverride( name = "userId", column = @Column(name = "classStaffId"))
	private User staffInClass;
    
    
   /* @ManyToOne
	@JoinColumn(name="class_staff_id", nullable=false)
    @javax.persistence.Embedded
	@AttributeOverride( name = "userId", column = @Column(name = "class_staff_id"))	
    private UserRoleMap staffInClass;
    
    
    @OneToMany 
	@JoinTable(name=" tbl_lms_userrole_map",
               joinColumns={@JoinColumn(name="user_id")},
               inverseJoinColumns={@JoinColumn(name="role_id")})
	//private Set<Role> UserRole = new HashSet<Role>();
    private Set<User> staffInClass;
    
    @ManyToOne
	@JoinTable(name=" tbl_lms_userrole_map",
               joinColumns={@JoinColumn(name="user_id")},
               inverseJoinColumns={@JoinColumn(name="role_id")})
	private Set<Role> UserRole = new HashSet<Role>();
    //private Set<User> user ;*/
    
    
    @Column(name="class_description")
    private String classDescription;
    
    @Column(name="class_comments")
    private String classComments;
    
    @Column(name="class_notes")
    private String classNotes;
    
    @Column(name="class_recording_path")
    private String classRecordingPath;
    
    @Column(name="creation_time")
	private Timestamp creationTime;
	
	@Column(name="last_mod_time")
	private Timestamp lastModTime;
}
