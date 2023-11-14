package com.numpyninja.lms.config;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(username="sailaja@gmail.com", password = "utta^3", roles="USER")
public @interface WithMockStudent {
}
