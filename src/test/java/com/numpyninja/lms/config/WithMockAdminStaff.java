package com.numpyninja.lms.config;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username="Tim@gmail.com", password = "vision02", roles={ "ADMIN", "STAFF"})
public @interface WithMockAdminStaff {
}
