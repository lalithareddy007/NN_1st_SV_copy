package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.repository.ProgramRepository;
import com.numpyninja.lms.services.ProgramServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(ProgramController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
public class ProgramControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProgramRepository programRepository;

    ObjectMapper obj = new ObjectMapper();

    private String token;

    @BeforeEach
    public void setup() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        final LoginDto loginDto = new LoginDto("John.Matthew@gmail.com", "John123");

        final String  responseBody = obj.writeValueAsString(loginDto);

        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(responseBody))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());

        final JwtResponseDto jwtResponseDto = obj.readValue(mvcResult.getResponse().getContentAsString(),
                JwtResponseDto.class);
        token = jwtResponseDto.getToken();

        assertNotNull(token, "token is null");
    }


    @Test
    public void testGetAllPrograms() throws Exception {
        System.out.println(token);
        final MvcResult mvcResult = mockMvc.perform(get("/lms/allPrograms").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void testAllProgramsWithUser() throws Exception{
        final MvcResult mvcResult=mockMvc.perform(get("/lms/allProgramsWithUsers").contextPath("/lms")
                .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
    @Test
    public void testGetProgramById() throws Exception{

        long programId=programRepository.findAll().get(0).getProgramId();
        System.out.println(programId);
        final MvcResult mvcResult=mockMvc.perform(get("/lms/programs/{programId}",programId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
         String responseBody = mvcResult.getResponse().getContentAsString();
         ProgramDTO programDTO=obj.readValue(responseBody, ProgramDTO.class);
        assertEquals(programId,programDTO.getProgramId());
    }
    @Test
    @Transactional
    @DirtiesContext
    public void testCreateAndSaveProgram() throws Exception{
        ProgramDTO dummyProgramDto=new ProgramDTO();
        dummyProgramDto.setProgramName("C++");
        dummyProgramDto.setProgramId(250L);
        dummyProgramDto.setProgramDescription("Beginner");
        dummyProgramDto.setProgramStatus("Active");
        dummyProgramDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
        dummyProgramDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        String requestJson =obj.writeValueAsString(dummyProgramDto);
        final MvcResult mvcResult=mockMvc.perform(post("/lms/saveprogram").contextPath("/lms").content(requestJson)
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
        assertEquals(201, mvcResult.getResponse().getStatus());

    }

    @Test
    @Transactional
    @DirtiesContext
    public void testUpdateProgramById() throws  Exception{
        long programId=programRepository.findAll().get(0).getProgramId();
        ProgramDTO dummyProgramDto=new ProgramDTO();
        dummyProgramDto.setProgramDescription("Advance");
        dummyProgramDto.setProgramName("PostgreSQL");
        dummyProgramDto.setProgramId(250L);
        dummyProgramDto.setProgramStatus("Active");
        dummyProgramDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
        dummyProgramDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        String requestJson =obj.writeValueAsString(dummyProgramDto);
        final MvcResult mvcResult=mockMvc.perform(put("/lms/putprogram/{programId}",programId).contextPath("/lms").content(requestJson)
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ProgramDTO programDTO=obj.readValue(responseBody, ProgramDTO.class);
        assertEquals(200,mvcResult.getResponse().getStatus());
        assertEquals(dummyProgramDto.getProgramDescription(),programDTO.getProgramDescription());

    }
    @Test
    @Transactional
    @DirtiesContext
    public void testUpdateProgramByProgramName() throws  Exception{
        String programName="Postgres";
        ProgramDTO dummyProgramDto=new ProgramDTO();
        dummyProgramDto.setProgramDescription("Advance");
        dummyProgramDto.setProgramName("PostgreSQL");
        dummyProgramDto.setProgramId(250L);
        dummyProgramDto.setProgramStatus("Active");
        dummyProgramDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
        dummyProgramDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        String requestJson =obj.writeValueAsString(dummyProgramDto);
        final MvcResult mvcResult=mockMvc.perform(put("/lms/program/{programName}",programName).contextPath("/lms").content(requestJson)
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        ProgramDTO programDTO=obj.readValue(responseBody, ProgramDTO.class);
        assertEquals(200,mvcResult.getResponse().getStatus());
        assertEquals(dummyProgramDto.getProgramDescription(),programDTO.getProgramDescription());

    }
    @Test
    @Transactional
    public void testDeleteByProgramId() throws Exception{
        long programId=programRepository.findAll().get(0).getProgramId();
        final MvcResult mvcResult=mockMvc.perform(delete("/lms/deletebyprogid/{programId}",programId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
        assertEquals(200,mvcResult.getResponse().getStatus());

    }
    @Test
    @Transactional
    public void testDeleteByProgramName() throws Exception{
        String programName="Postgres";
        final MvcResult mvcResult=mockMvc.perform(delete("/lms/deletebyprogname/{programName}",programName).contextPath("/lms")
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
        assertEquals(200,mvcResult.getResponse().getStatus());

    }
}
































