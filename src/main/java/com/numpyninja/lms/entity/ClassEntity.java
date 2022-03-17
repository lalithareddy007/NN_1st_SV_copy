package com.numpyninja.lms.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tbl_lms_class_sch")
public class ClassEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_id_generator")
    @SequenceGenerator(name = "class_id_generator", sequenceName = "tbl_lms_class_sch_cs_id_seq", allocationSize = 1)
    Long csId;
    Integer batchId;
    Integer classNo;
    @DateTimeFormat(pattern = "dd/MM/yyyy h:mm a")
    Date classDate;
    String classTopic;
    String classStaffId;
    String classDescription;
    String classComments;
    String classNotes;
    String classRecordingPath;
}
