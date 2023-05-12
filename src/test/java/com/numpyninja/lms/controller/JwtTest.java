package com.numpyninja.lms.controller;

import com.numpyninja.lms.config.TestWebSecurityConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@Import(TestWebSecurityConfig.class)
public class JwtTest {

}
