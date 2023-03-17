package com.numpyninja.lms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_lms_submissions")
public class AssignmentSubmit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "submission_generator")
    @SequenceGenerator(name="submission_generator", sequenceName="tbl_lms_submissions_sub_id_seq", allocationSize = 1)
    @Column(name="sub_id")
    private Long submissionId;

    @ManyToOne
    @JoinColumn(name="sub_a_id", nullable=false)
    private Assignment assignment;

    @ManyToOne
    @JoinColumn(name="sub_student_id", nullable = false)
    private User user;

    @Column(name="sub_description")
    private String subDesc;

    @Column(name="sub_comments")
    private String subComments;

    @Column(name="sub_path_attach1")
    private String subPathAttach1;

    @Column(name="sub_path_attach2")
    private String subPathAttach2;

    @Column(name="sub_path_attach3")
    private String subPathAttach3;

    @Column(name="sub_path_attach4")
    private String subPathAttach4;

    @Column(name="sub_path_attach5")
    private String subPathAttach5;

    @Column(name="sub_datetime")
    private Timestamp subDateTime;

    @Column(name="graded_by")
    private String gradedBy;

    @Column(name="graded_datetime")
    private Timestamp gradedDateTime;

    @Column(name="grade")
    private int grade;

    @Column(name="creation_time")
    private Timestamp creationTime;

    @Column(name="last_mod_time")
    private Timestamp lastModTime;

}
