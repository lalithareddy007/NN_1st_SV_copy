package com.numpyninja.lms.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="tbl_lms_user_login")
public class UserLogin {
    @Id
    private String user_id;

    @Column(name="user_login_name")
    private String username;

    @Column(name="user_password")
    private String password;

    @Column(name="user_login_status")
    private String loginStatus;
}

