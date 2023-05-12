package com.numpyninja.lms.controller;

import com.numpyninja.lms.config.TestWebSecurityConfig;
import com.numpyninja.lms.services.UserServices;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import(TestWebSecurityConfig.class)
public class AbstractTestController {

    @MockBean
    private UserServices userService;
}
