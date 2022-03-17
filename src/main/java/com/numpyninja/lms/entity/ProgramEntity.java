package com.numpyninja.lms.entity;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tbl_lms_program")
public class ProgramEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "program_id_generator")
    @SequenceGenerator(name = "program_id_generator", sequenceName = "tbl_lms_program_program_id_seq", allocationSize = 1)
    Long programId;

    @NotBlank(message = "Program Name is mandatory")
    @Pattern(regexp = "([a-zA-Z0-9 ]+$)", message = "Program Name can contain only alphabets and numbers")
    @Length(min = 4, max = 25, message = "Program Name must be of min length 4 and max length 25")
    String programName;

    String programDescription;

    @NotBlank(message = "Program Status is mandatory")
    String programStatus;

}
