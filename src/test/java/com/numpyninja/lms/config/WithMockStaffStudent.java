package com.numpyninja.lms.config;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username="shital@gmail.com",password = "highsee@12", roles={"STAFF", "STUDENT"})
public @interface WithMockStaffStudent {
}
