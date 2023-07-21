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

    @Pattern(regexp = "([a-zA-Z0-9 ]+$)", message = "Program Name can contain only alphabets and numbers")
    @Length(min = 4, max = 25, message = "Program Name must be of min length 4 and max length 25")
    @Column(name="program_name")
    @NotNull
    private String programName;

    @NotBlank(message = "Program Description is mandatory")
    @Pattern (regexp="^[a-z0-9][a-z0-9_ ]*(?:-[a-z0-9]+)*$", message = "Program Desc can contain only alphabets and numbers")
    @Length(min = 4, max = 25, message = "Program Description must be of min length 4 and max length 25")
    @Column(name="program_description")
    private String programDescription;

    @NotBlank(message = "Program Status is mandatory")
    @Column(name="program_status")
    String programStatus;
    
    @Column(name="creation_time")
    //@JsonIgnore
	private Timestamp creationTime;

	@Column(name="last_mod_time")
	private Timestamp lastModTime;

}
