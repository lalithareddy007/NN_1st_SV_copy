package com.numpyninja.lms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.numpyninja.lms.config.UserIDGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="tbl_lms_user_login")
public class UserLogin {
   /* @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_login_id_generator")
    @SequenceGenerator(name = "user_login_id_generator", sequenceName = "tbl_lms_user_login_user_login_id_seq", allocationSize = 1)
    @Column( name ="user_login_id")
    private Long userLoginId;*/
   @Id
   private String userId;

    @Column(name="user_login_email")
    private String userLoginEmail;

    @Column(name="user_password")
    private String password;

    @Column(name="user_login_status")
    private String loginStatus;

    @Column
    @JsonIgnore
    private Timestamp creationTime;

    @Column
    @JsonIgnore
    private Timestamp lastModTime;

    // One-to-One relationship with UserLogin entity
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;



}