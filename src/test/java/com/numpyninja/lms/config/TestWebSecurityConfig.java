package com.numpyninja.lms.config;

import com.numpyninja.lms.security.WebSecurityConfig;
import com.numpyninja.lms.security.jwt.AuthEntryPointJwt;
import com.numpyninja.lms.security.jwt.JwtUtils;
import com.numpyninja.lms.services.UserServices;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration    // This Configuration is only for Test
public class TestWebSecurityConfig {

    @Bean
    public WebSecurityConfig webSecurityConfig() {
        return new WebSecurityConfig();
    }

    @Bean
    public AuthEntryPointJwt authEntryPointJwt() {
        return new AuthEntryPointJwt();
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

}
