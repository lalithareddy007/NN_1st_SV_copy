package com.numpyninja.lms.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tbl_lms_batch")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "batch_id_generator")
    @SequenceGenerator(name = "batch_id_generator", sequenceName = "tbl_lms_batch_batch_id_seq", allocationSize = 1)
    @Column(name="batch_id")
    Integer batchId;
    
    @Column(name="batch_name")
    String batchName;
   
    @Column(name="batch_description")
    String batchDescription;
    
    @Column(name="batch_status")
    String batchStatus;

	@ManyToOne ( fetch = FetchType.LAZY)        
    @JoinColumn ( name = "batch_program_id", nullable = false )                            
    private Program program;                         

	@Column(name="batch_no_of_classes")
    Integer batchNoOfClasses;
     
	@Column(name="creation_time")
	private Timestamp creationTime;

	@Column(name="last_mod_time")
	private Timestamp lastModTime;
}
