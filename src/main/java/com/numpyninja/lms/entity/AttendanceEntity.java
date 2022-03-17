package com.numpyninja.lms.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "tbl_lms_attendance")
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_id_generator")
    @SequenceGenerator(name = "attendance_id_generator", sequenceName = "tbl_lms_attendance_att_id_seq", allocationSize = 1)
    Long attId;

    @Column(name = "csId")
    Integer classId;
    String studentId;
    String attendance;
}
