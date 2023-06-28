package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//<<<<<<< HEAD
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.security.jwt.JwtUtils;
import com.numpyninja.lms.services.ProgBatchServices;
import com.numpyninja.lms.services.UserLoginService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//=======
import com.numpyninja.lms.dto.EmailDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.security.jwt.JwtUtils;
import com.numpyninja.lms.services.UserLoginService;
import com.numpyninja.lms.util.EmailSender;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
//>>>>>>> LMSPhase2
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
//<<<<<<< HEAD
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
//=======
//>>>>>>> LMSPhase2
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
//<<<<<<< HEAD
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//=======
//>>>>>>> LMSPhase2
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.ResultActions;
//<<<<<<< HEAD
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.util.StringUtils;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//=======

import java.util.Arrays;
import java.util.List;
//>>>>>>> LMSPhase2

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserLoginController.class)
public class UserLoginControllerTest extends AbstractTestController {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserLoginService userLoginService;
    
    @Mock
    private UserLoginRepository userLoginRepository;
    
    @Mock
    private JwtUtils jwtUtils;
    
    @Mock
    private EmailSender emailSender;
    
    @Mock
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    JwtUtils jwutils;


    @Test
    public void given_NonExistingUser_WhenLogin_ThenThrowException( ) throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUserLoginEmailId("test23@gmail.com");
        loginDto.setPassword( "test");

        //given
        given( userLoginService.signin( loginDto)).willThrow( new BadCredentialsException("Bad credentials"));

        //When
        ResultActions response = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDto)));

        //then
        response.andExpect( result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException)) ;
    }


    @Test
    public void given_ExistingUserWithValidPassword_WhenLogin_ThenReturnJwtResponse( ) throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUserLoginEmailId("vidya@gmail.com");
        loginDto.setPassword( "password");
        List<String> list = Arrays.asList( "ROLE_STAFF");
        JwtResponseDto jwtResponseDto = JwtResponseDto.builder().userId("U11").email("vidya@gmail.com")
                        .roles(list).token("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaGVudGhhbWFyYWkubjJAZ21haWwuY29YXq9-_xfLYNMMhapvw").build();
        //given
        given( userLoginService.signin( loginDto)).willReturn( jwtResponseDto);

        //When
        ResultActions response = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(loginDto)));
        //then
        response.andDo(print()).andExpect(status().isOk());
        response.andExpect(jsonPath("token", is(jwtResponseDto.getToken())))
                .andExpect(jsonPath("email", is(jwtResponseDto.getEmail())))
                .andExpect(jsonPath("userId", is(jwtResponseDto.getUserId())));
    }
    
    
    public void given_ForgotPassword_UserWithInValidEmail_ThenReturnJwtResponse( ) throws Exception{
    	EmailDto emailDto = new EmailDto();
    	emailDto.setUserLoginEmailId("Nisha@gmail.com");
    	JwtResponseDto jwtResponseDto = JwtResponseDto.builder().email("Nisha@gmail.com")
    			.token(null).status("Invalid Email").build();
    
    	//given
        given( userLoginService.forgotPasswordConfirmEmail( emailDto)).willThrow( new BadCredentialsException("Bad credentials"));

    	
    	//When
        ResultActions response = mockMvc.perform(post("/login/forgotpassword/confirmEmail")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(emailDto)));
        
        //then
        response.andExpect( result -> assertTrue(result.getResolvedException() instanceof BadCredentialsException)) ;
                
    }
    
    @Test
    public void given_ForgotPassword_UserWithValidEmail_ThenReturnJwtResponse() throws Exception {
        EmailDto emailDto = new EmailDto();
        emailDto.setUserLoginEmailId("Nisha@gmail.com");
        JwtResponseDto jwtResponseDto = JwtResponseDto.builder().email("Nisha@gmail.com")
                       .token("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaGVudGhhbWFyYWkubjJAZ21haWwuY29YXq9-_xfLYNMMhapvw").build();
        //given
        given( userLoginService.forgotPasswordConfirmEmail(emailDto)).willReturn( jwtResponseDto);

        //When
        ResultActions response = mockMvc.perform(post("/login/forgotpassword/confirmEmail")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(emailDto)));
        //then
        response.andDo(print()).andExpect(status().isCreated());
        response.andExpect(jsonPath("token", is(jwtResponseDto.getToken())))
                .andExpect(jsonPath("email", is(jwtResponseDto.getEmail())));
    }
   
      
   @Test
    void validateToken_ValidToken_Returns_ValidApi_Response() {
       
        String validToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
        UserLoginController userLoginController = new UserLoginController(userLoginService);
        
        //given
        
        Mockito.when(userLoginService.validateToken(validToken)).thenReturn(true);
       
        //when
        ResponseEntity<com.numpyninja.lms.config.ApiResponse> response = userLoginController.validateToken(validToken);

        // Assert
        Assertions.assertEquals("Valid", response.getBody().getMessage());
        Assertions.assertEquals(true, response.getBody().isSuccess());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    void validateToken_InvalidToken_Returns_InvalidApi_Response() {
        // given
        String invalidToken = "e yJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
        UserLoginController userLoginController = new UserLoginController(userLoginService);
        Mockito.when(userLoginService.validateToken(invalidToken)).thenReturn(false);

        // when
        ResponseEntity<com.numpyninja.lms.config.ApiResponse> response = userLoginController.validateToken(invalidToken);

        // then
        Assertions.assertEquals("InValid", response.getBody().getMessage());
        Assertions.assertEquals(false, response.getBody().isSuccess());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
           

//localhost:1234/lms/login/AccountActivation
    @Test
    public void testValidateAccountActToken_InvalidToken() throws Exception {
        String token = "eyJhbGciOiJIUzUxMi.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
        when(userLoginService.validateTokenAtAccountActivation(anyString()))
                .thenReturn("Invalid");

        ResultActions response= mockMvc.perform(get("/login/AccountActivation")
                .header("Authorization", "invalid_token")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid/Expired Token"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testValidateAccountActToken_AlreadyActivated() throws Exception {
       String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
        when(userLoginService.validateTokenAtAccountActivation(anyString()))
                .thenReturn("acctActivated already");

        ResultActions response  = mockMvc.perform(get("/login/AccountActivation")
                .header("Authorization", "valid_token")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("acctActivated already"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void testValidateAccountActToken_ValidToken() throws Exception {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
        when(userLoginService.validateTokenAtAccountActivation(anyString()))
                .thenReturn("alpha@gmail.com");

        ResultActions response  = mockMvc.perform(get("/login/AccountActivation")
                .header("Authorization", "valid_token")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("alpha@gmail.com"))
                .andExpect(jsonPath("$.success").value(true));

    }

    //ResetPassword
    @Test
    void testResetPassword_InvalidToken_ReturnsBadRequest() throws Exception {
        LoginDto loginDto = new LoginDto();
        String token = null;

        when(userLoginService.resetPassword(loginDto, token)).thenReturn("Invalid");

        ResultActions response=    mockMvc.perform(post("/resetPassowrd")
                        .header("Authorization", "invalid_token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)));

        response.andExpect(status().isUnauthorized());
    }

    @Test
    void testResetPassword_ValidToken_PasswordSaved_ReturnsOk() {
        //given
        LoginDto loginDto = new LoginDto();
        UserLoginController userLoginController = new UserLoginController(userLoginService);
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
        String status = "Password saved";
        when(userLoginService.resetPassword(loginDto, token)).thenReturn(status);

        // when
        ResponseEntity<ApiResponse> response = userLoginController.resetPassword(loginDto, token);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(status, response.getBody().getMessage());
        assertEquals(true, response.getBody().isSuccess());
        //verify(userLoginService, times(1)).resetPassword(loginDto, token);
    }


}







