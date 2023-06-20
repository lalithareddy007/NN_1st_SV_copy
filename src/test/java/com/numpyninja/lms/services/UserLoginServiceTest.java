package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;
import com.numpyninja.lms.security.UserDetailsImpl;
import com.numpyninja.lms.security.jwt.JwtUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        when(userCache.getUserFromCache(loginDto.getUserLoginEmailId())).thenReturn(userDetails);
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
        assertEquals(jwtResponseDtoGot.getEmail(), loginDto.getUserLoginEmailId());
        assertEquals(jwtResponseDtoGot.getToken(), jwtToken);
        assertEquals(jwtResponseDtoGot.getUserId(), "U11");
        assertEquals(jwtResponseDtoGot.getRoles(), Collections.singletonList("ROLE_STAFF"));
    }


//    void testvalidateTokenAtAccountActivation_Valid() throws Exception{
//
//        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
//       String emailID = "alpha@gmail.com";
//        Optional<UserLogin> user = null;
//        when(jwtUtils.validateAccountActivationToken(token)).thenReturn("Valid");
//        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn(emailID);
//        when(userLoginRepository.findByUserLoginEmailIgnoreCase(emailID)).thenReturn(user);
//
//        when(user.get().getPassword().isEmpty()).thenReturn(false);
//
//
//    }

//
//    @Test
//    public void testValidateTokenAtAccountActivation_InvalidToken() {
//       //String token = "InvalidToken";
//        String token = "eyJhbGciOiJIUzUxMi.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
//        when(jwtUtils.validateAccountActivationToken(token))
//                .thenReturn("InValid");
//        String validity = jwtUtils.validateAccountActivationToken(token);
//
//        Assertions.assertEquals("Invalid", validity);
//    }
//
//    @Test
//    public void testValidateTokenAtAccountActivation_AlreadyActivated() {
//        String token = "Bearer ValidToken";
//        String userName = "example@email.com";
//
//        when(jwtUtils.validateAccountActivationToken(token))
//                .thenReturn("Valid");
//        when(jwtUtils.getUserNameFromJwtToken(token))
//                .thenReturn(userName);
//
//        Optional<UserLogin> userOptional = Optional.of(new UserLogin());
//        when(userLoginRepository.findByUserLoginEmailIgnoreCase(userName))
//                .thenReturn(userOptional);
//
//        String validity = userLoginService.validateTokenAtAccountActivation(token);
//
//        Assertions.assertEquals("acctActivated already", validity);
//    }
//
//    @Test
//    public void testValidateTokenAtAccountActivation_ValidToken() {
//        String token = "Bearer ValidToken";
//        String userName = "example@email.com";
//
//        when(jwtUtils.validateAccountActivationToken(token))
//                .thenReturn("Valid");
//        when(jwtUtils.getUserNameFromJwtToken(token))
//                .thenReturn(userName);
//
//        when(userLoginRepository.findByUserLoginEmailIgnoreCase(userName))
//                .thenReturn(Optional.empty());
//
//        String validity = userLoginService.validateTokenAtAccountActivation(token);
//
//        Assertions. assertEquals(userName, validity);
//    }

//



    @Test
    public void testValidateTokenAtAccountActivation() {
        // Invalid token
        String invalidToken = "InvalidToken";
        String invalidValidity = "Invalid";
        assertEquals(invalidValidity, userLoginService.validateTokenAtAccountActivation(invalidToken));

        // Already activated account
        String activatedToken = "Bearer ValidToken";
        String activatedEmail = "test@example.com";
        String alreadyActivatedValidity = "acctActivated already";

        when(jwtUtils.validateAccountActivationToken(anyString()))
                .thenReturn("Valid");
        when(jwtUtils.getUserNameFromJwtToken(anyString()))
                .thenReturn(activatedEmail);

        Optional<UserLogin> activatedUserOptional = Optional.of(new UserLogin());
        when(userLoginRepository.findByUserLoginEmailIgnoreCase(activatedEmail))
                .thenReturn(activatedUserOptional);

        assertEquals(alreadyActivatedValidity, userLoginService.validateTokenAtAccountActivation(activatedToken));

        // Valid token
        String validToken = "Bearer ValidToken";
        String validEmail = "test@example.com";
        String validValidity = "Valid";

        when(jwtUtils.validateAccountActivationToken(anyString()))
                .thenReturn("Valid");
        when(jwtUtils.getUserNameFromJwtToken(anyString()))
                .thenReturn(validEmail);

        when(userLoginRepository.findByUserLoginEmailIgnoreCase(validEmail))
                .thenReturn(Optional.empty());

        assertEquals(validValidity, userLoginService.validateTokenAtAccountActivation(validToken));
    }
}





