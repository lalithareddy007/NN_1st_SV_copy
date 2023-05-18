package com.numpyninja.lms.services;

import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import com.numpyninja.lms.security.jwt.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceTest {
    @InjectMocks
    UserLoginService userLoginService;
    @Mock
    private UserLoginRepository userLoginRepository;
    @Mock
    private UserRoleMapRepository userRoleMapRepository;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    PasswordEncoder encoder;
    @Mock
    JwtUtils jwtUtils;

    @DisplayName("JUnit test for signin method for Valid LoginDetails")
    @Test
    public void givenValidLoginDetails_WhenSignIn_ReturnJwtResponseDto( ){

    }

}
