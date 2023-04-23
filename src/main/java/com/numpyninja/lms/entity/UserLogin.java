package com.numpyninja.lms.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="tbl_lms_user_login")
public class UserLogin  {
    @Id
    private String user_id;

    @Column(name="user_login_name")
    private String userLoginEmail;

    @Column(name="user_password")
    private String password;

    @Column(name="user_login_status")
    private String loginStatus;

}

