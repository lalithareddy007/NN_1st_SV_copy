package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.EmailDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.security.WebSecurityConfig;
import com.numpyninja.lms.security.jwt.AuthEntryPointJwt;
import com.numpyninja.lms.security.jwt.AuthTokenFilter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@AutoConfigureMockMvc
@WebMvcTest(UserLoginController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@ContextConfiguration(classes = {WebSecurityConfig.class})
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class UserLoginControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper obj = new ObjectMapper();

    private String token;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // fetch a user token
        token = generateToken("John.Matthew@gmail.com", "John123");
        assertNotNull(token, "token is null");
    }

    private String generateToken(String userName, String password) throws Exception {
        final LoginDto loginDto = new LoginDto(userName, password);
        final String loginBody = obj.writeValueAsString(loginDto);

        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(loginBody))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        String loginResponseBody = mvcResult.getResponse().getContentAsString();
        final JwtResponseDto jwtResponseDto = obj.readValue(loginResponseBody, JwtResponseDto.class);
        return jwtResponseDto.getToken();
    }

    @Test
    @Order(1)
    public void testSignIn() throws Exception {
        final LoginDto loginDto = new LoginDto();
        loginDto.setUserLoginEmailId("ravi@gmail.com");
        loginDto.setPassword("Ravi@123");

        String jsonRequest = obj.writeValueAsString(loginDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        JwtResponseDto jwtResponseDto = obj.readValue(jsonResponse, JwtResponseDto.class);
        assertNotNull(jwtResponseDto.getToken(), "JWT token is null");
        assertNotNull(jwtResponseDto.getRoles(), "Roles are null");

        //Expected list of roles for the user
        List<String> expectedRoles = Arrays.asList("ROLE_ADMIN");
        assertEquals(expectedRoles.size(), jwtResponseDto.getRoles().size(), "Number of roles mismatch");

        // Validate each role
        for (String expectedRole : expectedRoles) {
            assertTrue(jwtResponseDto.getRoles().contains(expectedRole), "Role not found: " + expectedRole);
        }
    }

    @Test
    @Order(2)
    public void testInvalidPasswordOrEmail() throws Exception {
        final LoginDto loginDto = new LoginDto();
        loginDto.setUserLoginEmailId("ravi@gmail.com");
        loginDto.setPassword("Ravi@1234");

        String jsonRequest = obj.writeValueAsString(loginDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(401, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Bad credentials", message);
    }

    @Test
    @Order(3)
    public void testValidateToken() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/validateToken").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(true, apiResponse.isSuccess());
        assertEquals("Valid", message);
    }

    @Test
    @Order(4)
    public void testInvalidToken() throws Exception {
        // Use an invalid or expired token
        String invalidToken = "invalid_token";

        final MvcResult mvcResult = mockMvc.perform(get("/lms/validateToken").contextPath("/lms")
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(401, mvcResult.getResponse().getStatus());
    }


    @Test
    @Order(5)
    public void  testValidateAccountActToken() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/login/AccountActivation").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("acctActivated already", message);
    }

    @Test
    @Order(6)
    public void testValidateAccForInvalidToken() throws Exception {
        String invalidToken = "invalid_token";
        final MvcResult mvcResult = mockMvc.perform(get("/lms/login/AccountActivation").contextPath("/lms")
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Invalid/Expired Token", message);
    }


    @Test
    @Order(7)
    public void testForgotPasswordForEmailId() throws Exception {
        final EmailDto emailDto = new EmailDto();
        emailDto.setUserLoginEmailId("ravi@gmail.com");

        String jsonRequest = obj.writeValueAsString(emailDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/login/forgotpassword/confirmEmail")
                        .contextPath("/lms")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        assertEquals(201, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(8)
    public void testForgotPasswordForInvalidEmail() throws Exception {
        final EmailDto emailDto = new EmailDto();
        emailDto.setUserLoginEmailId("ravi123@gmail.com");

        String jsonRequest = obj.writeValueAsString(emailDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/login/forgotpassword/confirmEmail")
                        .contextPath("/lms")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        assertEquals(401, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(9)
    public void testLogout() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/logoutlms").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(10)
    public void testLogoutForInvalidToken() throws Exception {
        String invalidToken = "invalid_token";
        final MvcResult mvcResult = mockMvc.perform(get("/lms/logoutlms").contextPath("/lms")
                        .header("Authorization", "Bearer " + invalidToken))
                .andReturn();

        int statusCode = mvcResult.getResponse().getStatus();
        assertEquals(401, statusCode);
    }
}
