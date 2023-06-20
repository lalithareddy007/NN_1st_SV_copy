package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import com.numpyninja.lms.security.UserDetailsImpl;
import com.numpyninja.lms.security.jwt.JwtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceTest  {
    @InjectMocks
    UserLoginService userLoginService;
    @Mock
    private UserLoginRepository userLoginRepository;
    @Mock
    private UserRoleMapRepository userRoleMapRepository;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserCache userCache;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("JUnit test for signin method for Valid LoginDetails")
    @Test
    public void givenValidLoginDetails_WhenSignIn_ReturnJwtResponseDto() {
        LoginDto loginDto = new LoginDto();
        loginDto.setUserLoginEmailId("vijaybharathi@gmail.com");
        loginDto.setPassword("lksez$");

        List authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_STAFF"));

        //given
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        when( userCache.getUserFromCache( loginDto.getUserLoginEmailId())).thenReturn( userDetails);
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String jwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaGVudGhhbWFyYWkubjJAZ21haWwuY29tIiwiaWF0IjoxNjg0NDU0NDYwLCJleH";
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwtToken);


        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUserId()).thenReturn("U11");

        when(userDetails.getAuthorities()).thenReturn(authorities);

        //when
        JwtResponseDto jwtResponseDtoGot = userLoginService.signin(loginDto);

        //then
        Assertions.assertEquals(jwtResponseDtoGot.getEmail(), loginDto.getUserLoginEmailId());
        Assertions.assertEquals(jwtResponseDtoGot.getToken(), jwtToken);
        Assertions.assertEquals(jwtResponseDtoGot.getUserId(), "U11");
        Assertions.assertEquals(jwtResponseDtoGot.getRoles(), Collections.singletonList("ROLE_STAFF"));
    }


}
