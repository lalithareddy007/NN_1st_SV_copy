package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.dto.UserRoleProgramBatchMapDto;
import com.numpyninja.lms.repository.UserRoleProgramBatchMapRepository;
import com.numpyninja.lms.security.WebSecurityConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(UserRoleProgramBatchMapController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@ContextConfiguration(classes = {WebSecurityConfig.class})
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class UserRoleProgramBatchMapControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper obj = new ObjectMapper();

    private String token;

    @Autowired
    private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        // fetch a token
        final LoginDto loginDto = new LoginDto("John.Matthew@gmail.com", "John123");
        final String loginBody = obj.writeValueAsString(loginDto);

        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(loginBody))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        String loginResponseBody = mvcResult.getResponse().getContentAsString();
        final JwtResponseDto jwtResponseDto = obj.readValue(loginResponseBody, JwtResponseDto.class);
        token = jwtResponseDto.getToken();

        assertNotNull(token, "token is null");
    }


    @Test
    @Order(1)
    public void testGetAll() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/userRoleProgramBatchMap").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }


    @Test
    @Order(2)
    public void testGetById() throws Exception
    {
        String userId = userRoleProgramBatchMapRepository.findAll().get(0).getUser().getUserId();
        final MvcResult mvcResult = mockMvc.perform(get("/lms/userRoleProgramBatchMap/"+userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(200,mvcResult.getResponse().getStatus());

        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos = obj.readValue(responseBody,new TypeReference<List<UserRoleProgramBatchMapDto>>() {});
        assertEquals(userId,userRoleProgramBatchMapDtos.get(0).getUserId());
    }


    @Test
    @Order(3)
    public void testGetByInvalidId() throws Exception
    {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/userRoleProgramBatchMap/"+"U10").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(404,mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(responseBody, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("UserRoleProgramBatchMap not found with Id : U10 ",message);
    }


    @Test
    @Order(4)
    public void testdeleteAllProgramBatchesAssignedToAUser() throws Exception
    {
        String userId = userRoleProgramBatchMapRepository.findAll().get(0).getUser().getUserId();
        final MvcResult mvcResult = mockMvc.perform(delete("/lms/userRoleProgramBatchMap/deleteAll/"+userId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        assertEquals(200,mvcResult.getResponse().getStatus());
    }

}
