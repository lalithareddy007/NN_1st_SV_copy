package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.dto.SkillMasterDto;
import com.numpyninja.lms.repository.SkillMasterRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.numpyninja.lms.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;



import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(UserSkillController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class SkillMasterControllerIntegrationTest {
    private static MockMvc mockMvc;
    private static String token;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SkillMasterRepository skillMasterRepository;

    @Autowired
    private UserRepository userRepository;
    ObjectMapper obj = new ObjectMapper();

    @Autowired
    private EntityManager entityManager;
    private static Long skillId;


    @BeforeEach
    public void beforeSetup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        // fetch a token
        final LoginDto loginDto = new LoginDto("John.Matthew@gmail.com", "John123");
        //convert obj to json or string
        final String loginBody = obj.writeValueAsString(loginDto);

        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(loginBody))
                .andReturn();
        // validating the expected value and actual value
        assertEquals(200, mvcResult.getResponse().getStatus());

        //convert json to obj
        String loginResponseBody = mvcResult.getResponse().getContentAsString();
        final JwtResponseDto jwtResponseDto = obj.readValue(loginResponseBody, JwtResponseDto.class);
        token = jwtResponseDto.getToken();
        assertNotNull(token, "token is null");
    }
    @Test
    @Order(1)
    public void createAndSaveSkillTest() throws Exception
    {
        final SkillMasterDto skillMasterDto;
        skillMasterDto = new SkillMasterDto();
        skillMasterDto.setSkillName("TestIntegration");
        skillMasterDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        skillMasterDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));

        String jsonRequest = obj.writeValueAsString(skillMasterDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/SaveSkillMaster/").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(201, mvcResult.getResponse().getStatus());
        SkillMasterDto skillMastreDto = obj.readValue(jsonResponse, SkillMasterDto.class);
        assertEquals("TestIntegration", skillMastreDto.getSkillName());

        // Retrieve the SkillDTO with the generated ID
        assertNotNull(skillMastreDto.getSkillId(), "SkillId is null");
        skillId = skillMastreDto.getSkillId();
        System.out.println("Skill ID"+ skillId);
    }

    @Test
    @Order(2)
    public void createAndSaveSkillDuplicateSkillTest() throws Exception
    {
        final SkillMasterDto skillMasterDto;
        skillMasterDto = new SkillMasterDto();
        skillMasterDto.setSkillName("TestIntegration");
        skillMasterDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        skillMasterDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));

        String jsonRequest = obj.writeValueAsString(skillMasterDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/SaveSkillMaster/").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("cannot create skillMaster , since already exists", message);
    }
    @Test
    @Order(3)
    public void testGetAllSkills() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/allSkillMaster").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(4)
    public void testgetOneSkillByName() throws Exception{
        String skillName="TestIntegration";
        final MvcResult mvcResult = mockMvc.perform(get("/lms/skills/{skillMasterName}", skillName).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
    @Test
    @Order(5)
    public void testgetOneSkillByName_notFound() throws Exception{
        String skillName="IntegrationTestSample";
        final MvcResult mvcResult = mockMvc.perform(get("/lms/skills/{skillMasterName}", skillName).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();
        assertEquals(false, apiResponse.isSuccess());
    }

    @Test
    @Order(6)
    public void testupdateSkillById() throws Exception{
        System.out.println("SkillId"+skillId);
        final SkillMasterDto skillMasterDto;
        skillMasterDto = new SkillMasterDto();
        skillMasterDto.setSkillName("TestIntegrationUpdate");
        skillMasterDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        skillMasterDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));

        String jsonRequest = obj.writeValueAsString(skillMasterDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/updateSkills/{skillId}" + skillId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();
        assertEquals(false, apiResponse.isSuccess());
        assertEquals("TestIntegrationUpdate", skillMasterDto.getSkillName());

    }




}

