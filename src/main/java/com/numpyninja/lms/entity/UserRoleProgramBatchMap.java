package com.numpyninja.lms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity

@Table(name = "tbl_lms_user_role_program_batch_map")
public class UserRoleProgramBatchMap {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ur_pb_id_generator")
    @SequenceGenerator(name = "ur_pb_id_generator", sequenceName = "tbl_lms_user_role_program_batch_map_ur_pb_id_seq",
            allocationSize = 1)
    @Column(name ="ur_pb_id")
    private Long userRoleProgramBatchId;

    @ManyToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @ManyToOne(cascade=CascadeType.REMOVE)
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    @Column(name ="user_role_program_batch_status")
    private String userRoleProgramBatchStatus;

    @JsonIgnore
    @Column(name ="creation_time")
    private Timestamp creationTime;

    @JsonIgnore
    @Column(name ="last_mod_time")
    private Timestamp lastModTime;
}
