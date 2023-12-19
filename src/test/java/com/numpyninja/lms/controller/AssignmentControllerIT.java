package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(AssignmentController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@ContextConfiguration(classes = {WebSecurityConfig.class})
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
    public void testCreateAssignment() throws Exception {
        final AssignmentDto assignmentDto = new AssignmentDto();
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
        assertEquals(1, responseDto.getBatchId());
        assertEquals("U01", responseDto .getGraderId());
        assertNotNull(responseDto.getBatchId(), "batch id is null");
        assertNotNull(responseDto.getGraderId(), "grader id is null");

        assignmentId = responseDto.getAssignmentId();
    }

    @Test
    @Order(2)
    public void testAlreadyExistAssignment() throws Exception {
        final AssignmentDto assignmentDto = new AssignmentDto();
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
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Assignment already exists with given Name : DA Assignment ", message);
    }

    @Test
    @Order(3)
    public void testCreatedByUser() throws Exception {
        final AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setAssignmentName("DA Assignment4");
        assignmentDto.setAssignmentDescription("DA Practice");
        assignmentDto.setComments("New assignment created");
        assignmentDto.setPathAttachment1("FilePath1");
        assignmentDto.setPathAttachment2("FilePath2");
        assignmentDto.setPathAttachment3("FilePath3");
        assignmentDto.setPathAttachment4("FilePath4");
        assignmentDto.setPathAttachment5("FilePath5");
        assignmentDto.setCreatedBy("U04");
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
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User not found with Role(Admin/Staff) : U04 ", message);
    }


    @Test
    @Order(4)
    public void testValidRoleId() throws Exception {
        final AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setAssignmentName("DA Assignment5");
        assignmentDto.setAssignmentDescription("DA Practice");
        assignmentDto.setComments("New assignment created");
        assignmentDto.setPathAttachment1("FilePath1");
        assignmentDto.setPathAttachment2("FilePath2");
        assignmentDto.setPathAttachment3("FilePath3");
        assignmentDto.setPathAttachment4("FilePath4");
        assignmentDto.setPathAttachment5("FilePath5");
        assignmentDto.setCreatedBy("U02");
        assignmentDto.setBatchId(1);
        assignmentDto.setGraderId("U20");
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
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User not found with ID : U20 ", message);
    }

    @Test
    @Order(5)
    public void testGetAllAssignments() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/assignments").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(6)
    public void testGetAssignmentById() throws Exception {
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
    @Order(7)
    public void testGetAssignmentByInvalidId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/assignments/" + 10)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Assignment not found with Id : 10 ", message);
    }

    @Test
    @Order(8)
    public void testUpdateAssignment() throws Exception {
        final AssignmentDto assignmentDto = new AssignmentDto();
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
    @Order(9)
    public void testAssignmentIdExist() throws Exception {
        final AssignmentDto assignmentDto = new AssignmentDto();
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
        MvcResult mvcResult = mockMvc.perform(put("/lms/assignments/" + 6)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Assignment not found with Id : 6 ", message);
    }

    @Test
    @Order(10)
    public void testUpdatedByUser() throws Exception {
        final AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setAssignmentName("DA Assignment2");
        assignmentDto.setAssignmentDescription("DA Practice");
        assignmentDto.setComments("New assignment created");
        assignmentDto.setPathAttachment1("FilePath1");
        assignmentDto.setPathAttachment2("FilePath2");
        assignmentDto.setPathAttachment3("FilePath3");
        assignmentDto.setPathAttachment4("FilePath4");
        assignmentDto.setPathAttachment5("FilePath5");
        assignmentDto.setCreatedBy("U04");
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
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User not found with Role(Admin/Staff) : U04 ", message);
    }

    @Test
    @Order(11)
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
    @Order(12)
    public void testGetAssignmentByInvalidBatchId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/assignments/batch/" + 5).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        String jsonResponse  = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Assignments not found with BatchId : 5 ", message);
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void testDeleteAssignment() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(delete("/lms/assignments/" + assignmentId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
