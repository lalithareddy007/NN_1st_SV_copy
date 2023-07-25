package com.numpyninja.lms.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tbl_lms_program")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "program_id_generator")
    @SequenceGenerator(name = "program_id_generator", sequenceName = "tbl_lms_program_program_id_seq", allocationSize = 1)
    @Column(name="program_id")
    private Long programId;

    @NotBlank(message = "Program Name is mandatory")
    @Column(name="program_name")
    @NotNull
    private String programName;

    @NotBlank(message = "Program Description is mandatory")
    @Column(name="program_description")
    private String programDescription;

    @NotBlank(message = "Program Status is mandatory")
    @Column(name="program_status")
    String programStatus;
    
    @Column(name="creation_time")
	private Timestamp creationTime;

	@Column(name="last_mod_time")
	private Timestamp lastModTime;

}
