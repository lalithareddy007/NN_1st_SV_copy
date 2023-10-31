package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(AssignmentController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class AssignmentControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper obj = new ObjectMapper();

    private String token;

    private static Long assignmentId;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

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
    public void testCreateAssignment() throws Exception {

        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setAssignmentName("DA Assignment");
        assignmentDto.setAssignmentDescription("DA Practice");
        assignmentDto.setComments("New assignment created");
        assignmentDto.setPathAttachment1("FilePath1");
        assignmentDto.setPathAttachment2("FilePath2");
        assignmentDto.setPathAttachment3("FilePath3");
        assignmentDto.setPathAttachment4("FilePath4");
        assignmentDto.setPathAttachment5("FilePath5");
        assignmentDto.setCreatedBy("U01");
        assignmentDto.setBatchId(1);
        assignmentDto.setGraderId("U01");
        Calendar calendar = (Calendar) Calendar.getInstance();
        calendar.set(Calendar.MONTH, 10);
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.DAY_OF_WEEK, 14);
        Date dt = calendar.getTime();
        assignmentDto.setDueDate(dt);

        String jsonRequest = obj.writeValueAsString(assignmentDto);
        MvcResult mvcResult = mockMvc.perform(post("/lms/assignments").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(201, mvcResult.getResponse().getStatus());

        AssignmentDto  responseDto = obj.readValue( jsonResponse ,AssignmentDto.class);
        assertEquals("DA Assignment", responseDto .getAssignmentName());
        assertEquals("DA Practice", responseDto .getAssignmentDescription());
        assertEquals("New assignment created", responseDto .getComments());
        assertEquals("FilePath1", responseDto .getPathAttachment1());
        assertEquals("FilePath2", responseDto .getPathAttachment2());
        assertEquals("FilePath3", responseDto .getPathAttachment3());
        assertEquals("FilePath4", responseDto .getPathAttachment4());
        assertEquals("FilePath5", responseDto .getPathAttachment5());
        assertEquals("U01", responseDto .getCreatedBy());
        assertEquals(1, responseDto .getBatchId());
        assertEquals("U01", responseDto .getGraderId());

        assignmentId = responseDto.getAssignmentId();
    }

    @Test
    @Order(2)
    public void testGetAllAssignments() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/assignments").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(3)
    public void getAssignmentById() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/assignments/" + assignmentId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        AssignmentDto  responseDto = obj.readValue( jsonResponse ,AssignmentDto.class);
        assertEquals("DA Assignment", responseDto.getAssignmentName());
        assertEquals("DA Practice", responseDto.getAssignmentDescription());
        assertEquals("New assignment created", responseDto.getComments());
        assertEquals("FilePath1", responseDto.getPathAttachment1());
        assertEquals("FilePath2", responseDto.getPathAttachment2());
        assertEquals("FilePath3", responseDto.getPathAttachment3());
        assertEquals("FilePath4", responseDto.getPathAttachment4());
        assertEquals("FilePath5", responseDto.getPathAttachment5());
        assertEquals("U01", responseDto.getCreatedBy());
        assertEquals(1, responseDto.getBatchId());
        assertEquals("U01", responseDto.getGraderId());
    }

    @Test
    @Order(4)
    public void testUpdateAssignment() throws Exception {

        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setAssignmentName("DA Assignment2");
        assignmentDto.setAssignmentDescription("DA Practice");
        assignmentDto.setComments("New assignment created");
        assignmentDto.setPathAttachment1("FilePath1");
        assignmentDto.setPathAttachment2("FilePath2");
        assignmentDto.setPathAttachment3("FilePath3");
        assignmentDto.setPathAttachment4("FilePath4");
        assignmentDto.setPathAttachment5("FilePath5");
        assignmentDto.setCreatedBy("U01");
        assignmentDto.setBatchId(1);
        assignmentDto.setGraderId("U01");
        Calendar calendar = (Calendar) Calendar.getInstance();
        calendar.set(Calendar.MONTH, 10);
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.DAY_OF_WEEK, 14);
        Date dt = calendar.getTime();
        assignmentDto.setDueDate(dt);

        String jsonRequest = obj.writeValueAsString(assignmentDto);
        MvcResult mvcResult = mockMvc.perform(put("/lms/assignments/" + assignmentId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        AssignmentDto  responseDto = obj.readValue( jsonResponse ,AssignmentDto.class);
        assertEquals("DA Assignment2",  responseDto.getAssignmentName());
    }

    @Test
    @Order(5)
    public void testGetAssignmentsForBatch() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/assignments/batch/" + 1).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse  = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<AssignmentDto> assignmentDtoList = obj.readValue(jsonResponse, new TypeReference<List<AssignmentDto>>() {
        });
        AssignmentDto assignmentDtoForAssignmentId = null;
        for(AssignmentDto assignmentDto: assignmentDtoList){
            if (assignmentId.equals(assignmentDto.getAssignmentId())){
                assignmentDtoForAssignmentId = assignmentDto;
            }
        }
            assertNotNull(assignmentDtoForAssignmentId, "assignmentDtoForAssignmentID is null");
            assertEquals("DA Assignment2", assignmentDtoForAssignmentId.getAssignmentName());
            assertEquals("DA Practice", assignmentDtoForAssignmentId.getAssignmentDescription());
            assertEquals("New assignment created", assignmentDtoForAssignmentId.getComments());
            assertEquals("FilePath1", assignmentDtoForAssignmentId.getPathAttachment1());
            assertEquals("FilePath2", assignmentDtoForAssignmentId.getPathAttachment2());
            assertEquals("FilePath3", assignmentDtoForAssignmentId.getPathAttachment3());
            assertEquals("FilePath4", assignmentDtoForAssignmentId.getPathAttachment4());
            assertEquals("FilePath5", assignmentDtoForAssignmentId.getPathAttachment5());
            assertEquals("U01", assignmentDtoForAssignmentId.getCreatedBy());
            assertEquals(1, assignmentDtoForAssignmentId.getBatchId());
            assertEquals("U01", assignmentDtoForAssignmentId.getGraderId());
    }

    @Test
    @Order(6)
    public void testDeleteAssignment() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(delete("/lms/assignments/" + assignmentId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
