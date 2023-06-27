package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

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
//    @Test
//    public void given_TokenFromValidUser_WhenClickonResetLink_ThenReturnValidity() throws Exception
//    {
//        String token ="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuaXRAZ21haWwuY29tIiwiaWF0IjoxNjg2MTQzMjYwLCJleHAiOjE2ODYzMTYwNjB9.QvVEiYYLxxRjAqAyrZJdSROWAQ3gP0o5uxez_Ar1Z-9MFkRXuSXt3ANok_LaZmzjKYa9d2q5DDvn3v1npgR3Kw";
//        String validity = "success";
//
//        //given
//        given( userLoginService.validateAccountActToken( token)).willReturn( "validity");
//
//        //when
//       ResultActions response = mockMvc.perform(get("/login/AccountActivation")
//                       .header("Authorization", "Bearer " + token))
//               .andExpect(status().isOk());
//               // .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(token)));
//
//       //then
//
//        response.andDo(print()).andExpect(status().isOk());
//        //response.andExpect(jsonPath("token", is(jwtResponseDto.getToken())))
//          //      .andExpect(jsonPath("email
//          ", is(jwtResponseDto.getEmail())))
//            //    .andExpect(jsonPath("userId", is(jwtResponseDto.getUserId())));

//
//      //  mvc.perform(MockMvcRequestBuilders.get("/test")
//        //                .header("Authorization", "Bearer " + token))
//          //      .andExpect(status().isOk());
//
//      //  response.andExpect(status().isOk())
//        //        .andDo(print());
//               // .andExpect(jsonPath("$", hasSize(token.length())));
//
//       // ResultActions resultActions = mockMvc.perform(get("/attendance/{id}", attId));
//      //  response.andExpect(status().isOk()).andDo(print())
//        //        .andExpect(MockMvcResultMatchers.jsonPath("$.token", equalTo(token)));
////        response.andDo(print()).andExpect(status().isOk());
////        ResultActions token1 = response.andExpect(jsonPath("$","token");
////        //      .andExpect(jsonPath("email", is(jwtResponseDto.getEmail())))
////            //    .andExpect(jsonPath("userId", is(jwtResponseDto.getUserId())));

//    }
//
}
