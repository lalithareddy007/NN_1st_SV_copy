package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
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

import javax.swing.plaf.PanelUI;
import java.io.DataInput;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(AssignmentSubmitController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class AssignmentSubmitControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper obj = new ObjectMapper();

    private String token;

    private static String userId;

    private static Long submissionId;

    private Long assignmentId;

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
    public void testSubmitAssignment() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(44L);
        assignmentSubmitDTO.setUserId("U06");
        assignmentSubmitDTO.setSubDesc("Submission of data science assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy(null);
        assignmentSubmitDTO.setGradedDateTime(null);
        assignmentSubmitDTO.setGrade(-1);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(post("/lms/assignmentsubmission").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(201, mvcResult.getResponse().getStatus());

        AssignmentSubmitDTO responseDto = obj.readValue(jsonResponse, AssignmentSubmitDTO.class);
        assertEquals(44, responseDto.getAssignmentId());
        assertNotNull(responseDto.getAssignmentId(), "Assignment id is null");
        assertEquals("U06", responseDto.getUserId());
        assertNotNull(responseDto.getUserId(), "User id is null");
        assertEquals("Submission of data science assignment", responseDto.getSubDesc());
        assertEquals("Submission of project", responseDto.getSubComments());
        assertEquals("Filepath1", responseDto.getSubPathAttach1());
        assertEquals("Filepath2", responseDto.getSubPathAttach2());
        assertEquals("Filepath3", responseDto.getSubPathAttach3());
        assertEquals("Filepath4", responseDto.getSubPathAttach4());
        assertEquals("Filepath5", responseDto.getSubPathAttach5());
        assertNotNull(responseDto.getSubDateTime(), "Submitted date is null");

        userId = responseDto.getUserId();
        submissionId = responseDto.getSubmissionId();
        assignmentId = responseDto.getAssignmentId();
    }


    @Test
    @Order(2)
    public void testAlredySubmitUserId() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(44L);
        assignmentSubmitDTO.setUserId("U06");
        assignmentSubmitDTO.setSubDesc("Submission of data science assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy(null);
        assignmentSubmitDTO.setGradedDateTime(null);
        assignmentSubmitDTO.setGrade(-1);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(post("/lms/assignmentsubmission").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Assignment with ID 44 already submitted by student "+ userId +
                ". Please visit 'Submissions' to resubmit assignment!", message);
    }

    @Test
    @Order(3)
    public void testSubmitAssignmentPostDueDate() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(5L);
        assignmentSubmitDTO.setUserId("U04");
        assignmentSubmitDTO.setSubDesc("Submission of SQL assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy(null);
        assignmentSubmitDTO.setGradedDateTime(null);
        assignmentSubmitDTO.setGrade(-1);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(post("/lms/assignmentsubmission").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Cannot submit assignment post due date", message);
    }

    @Test
    @Order(4)
    public void testGetAllSubmissions() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/assignmentsubmission").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(5)
    public void testGetSubmissionsByUserId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/assignmentsubmission/student/" + userId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(6)
    public void testGetSubmissionsByBatchId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/assignmentsubmission/studentbatch/" + 2)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<AssignmentSubmitDTO> assignmentSubmitDtOList = obj.readValue(jsonResponse,
                new TypeReference<List<AssignmentSubmitDTO>>() {
                });
        AssignmentSubmitDTO assignmentSubmitForSubmissionId = null;
        for(AssignmentSubmitDTO assignmentSubmitDTO : assignmentSubmitDtOList){
            if(submissionId.equals(assignmentSubmitDTO.getSubmissionId())){
                assignmentSubmitForSubmissionId = assignmentSubmitDTO;
                break;
            }
        }
        assertNotNull(assignmentSubmitForSubmissionId, "assignmentSubmitForSubmissionId is null");
        assertEquals(44,  assignmentSubmitForSubmissionId .getAssignmentId());
        assertEquals("U06",  assignmentSubmitForSubmissionId .getUserId());
        assertEquals("Submission of data science assignment",  assignmentSubmitForSubmissionId .getSubDesc());
        assertEquals("Submission of project",  assignmentSubmitForSubmissionId .getSubComments());
        assertEquals("Filepath1",  assignmentSubmitForSubmissionId .getSubPathAttach1());
        assertEquals("Filepath2",  assignmentSubmitForSubmissionId .getSubPathAttach2());
        assertEquals("Filepath3",  assignmentSubmitForSubmissionId .getSubPathAttach3());
        assertEquals("Filepath4",  assignmentSubmitForSubmissionId .getSubPathAttach4());
        assertEquals("Filepath5",  assignmentSubmitForSubmissionId .getSubPathAttach5());
    }

    @Test
    @Order(7)
    public void testResubmitAssignment() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(44L);
        assignmentSubmitDTO.setUserId("U06");
        assignmentSubmitDTO.setSubDesc("Submission of data science assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy(null);
        assignmentSubmitDTO.setGradedDateTime(null);
        assignmentSubmitDTO.setGrade(-1);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(put("/lms/assignmentsubmission/" + submissionId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }


    @Test
    @Order(8)
    public void testSubmissionNotFoundWithId() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(44L);
        assignmentSubmitDTO.setUserId("U06");
        assignmentSubmitDTO.setSubDesc("Submission of data science assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy(null);
        assignmentSubmitDTO.setGradedDateTime(null);
        assignmentSubmitDTO.setGrade(-1);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(put("/lms/assignmentsubmission/" + 7)
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
        assertEquals("Submission not found with ID : 7 ", message);
    }

    @Test
    @Order(9)
    public void testResubmittedBySameUserId() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(44L);
        assignmentSubmitDTO.setUserId("U05");
        assignmentSubmitDTO.setSubDesc("Submission of data science assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy(null);
        assignmentSubmitDTO.setGradedDateTime(null);
        assignmentSubmitDTO.setGrade(-1);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(put("/lms/assignmentsubmission/" + submissionId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Student with given ID U05 cannot submit this assignment", message);
    }

    @Test
    @Order(10)
    public void testResubmittedBySameAssignmentId() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(3L);
        assignmentSubmitDTO.setUserId("U06");
        assignmentSubmitDTO.setSubDesc("Submission of data science assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy(null);
        assignmentSubmitDTO.setGradedDateTime(null);
        assignmentSubmitDTO.setGrade(-1);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(put("/lms/assignmentsubmission/" + submissionId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Assignment with ID "+3+" is not part of this submission", message);
    }

    @Test
    @Order(11)
    public void testGradeAssignmentSubmission() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(44L);
        assignmentSubmitDTO.setUserId("U06");
        assignmentSubmitDTO.setSubDesc("Submission of data science assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy("U02");
        assignmentSubmitDTO.setGradedDateTime(Timestamp.valueOf("2023-11-11 02:10:00"));
        assignmentSubmitDTO.setGrade(299);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(put("/lms/assignmentsubmission/gradesubmission/" + submissionId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(12)
    public void testValidRoleId() throws Exception {
        final AssignmentSubmitDTO assignmentSubmitDTO = new AssignmentSubmitDTO();
        assignmentSubmitDTO.setAssignmentId(44L);
        assignmentSubmitDTO.setUserId("U06");
        assignmentSubmitDTO.setSubDesc("Submission of data science assignment");
        assignmentSubmitDTO.setSubComments("Submission of project");
        assignmentSubmitDTO.setSubPathAttach1("Filepath1");
        assignmentSubmitDTO.setSubPathAttach2("Filepath2");
        assignmentSubmitDTO.setSubPathAttach3("Filepath3");
        assignmentSubmitDTO.setSubPathAttach4("Filepath4");
        assignmentSubmitDTO.setSubPathAttach5("Filepath5");
        assignmentSubmitDTO.setSubDateTime(Timestamp.valueOf("2023-10-30 02:10:00"));
        assignmentSubmitDTO.setGradedBy("U04");
        assignmentSubmitDTO.setGradedDateTime(Timestamp.valueOf("2023-11-11 02:10:00"));
        assignmentSubmitDTO.setGrade(299);

        String jsonRequest = obj.writeValueAsString(assignmentSubmitDTO);
        MvcResult mvcResult = mockMvc.perform(put("/lms/assignmentsubmission/gradesubmission/" + submissionId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(jsonRequest))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User U04 is not allowed to grade the submission", message);
    }

    @Test
    @Order(13)
    public void testGetGradesByAssignmentId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/assignmentsubmission/getGrades/" + 44)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<AssignmentSubmitDTO> assignmentSubmitDtOList = obj.readValue(jsonResponse,
                new TypeReference<List<AssignmentSubmitDTO>>() {
                });
        AssignmentSubmitDTO assignmentSubmitForSubmissionId = null;
        for(AssignmentSubmitDTO assignmentSubmitDTO : assignmentSubmitDtOList){
            if(submissionId.equals(assignmentSubmitDTO.getSubmissionId())){
                assignmentSubmitForSubmissionId = assignmentSubmitDTO;
                break;
            }
        }
        assertNotNull(assignmentSubmitForSubmissionId, "assignmentSubmitForSubmissionId is null");
        assertEquals(44,  assignmentSubmitForSubmissionId .getAssignmentId());
        assertEquals("U06",  assignmentSubmitForSubmissionId .getUserId());
        assertEquals("Submission of data science assignment",  assignmentSubmitForSubmissionId .getSubDesc());
        assertEquals("Submission of project",  assignmentSubmitForSubmissionId .getSubComments());
        assertEquals("Filepath1",  assignmentSubmitForSubmissionId .getSubPathAttach1());
        assertEquals("Filepath2",  assignmentSubmitForSubmissionId .getSubPathAttach2());
        assertEquals("Filepath3",  assignmentSubmitForSubmissionId .getSubPathAttach3());
        assertEquals("Filepath4",  assignmentSubmitForSubmissionId .getSubPathAttach4());
        assertEquals("Filepath5",  assignmentSubmitForSubmissionId .getSubPathAttach5());
    }

    @Test
    @Order(14)
    public void testGetGradesByStudentId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/assignmentsubmission/grades/student/" + userId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(15)
    public void testGetGradesByBatchId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/assignmentsubmission/grades/" + 2)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<AssignmentSubmitDTO> assignmentSubmitDtOList = obj.readValue(jsonResponse,
                new TypeReference<List<AssignmentSubmitDTO>>() {
                });
        AssignmentSubmitDTO assignmentSubmitForSubmissionId = null;
        for(AssignmentSubmitDTO assignmentSubmitDTO : assignmentSubmitDtOList){
            if(submissionId.equals(assignmentSubmitDTO.getSubmissionId())){
                assignmentSubmitForSubmissionId = assignmentSubmitDTO;
                break;
            }
        }
        assertNotNull(assignmentSubmitForSubmissionId, "assignmentSubmitForSubmissionId is null");
        assertEquals(44,  assignmentSubmitForSubmissionId .getAssignmentId());
        assertEquals("U06",  assignmentSubmitForSubmissionId .getUserId());
        assertEquals("Submission of data science assignment",  assignmentSubmitForSubmissionId .getSubDesc());
        assertEquals("Submission of project",  assignmentSubmitForSubmissionId .getSubComments());
        assertEquals("Filepath1",  assignmentSubmitForSubmissionId .getSubPathAttach1());
        assertEquals("Filepath2",  assignmentSubmitForSubmissionId .getSubPathAttach2());
        assertEquals("Filepath3",  assignmentSubmitForSubmissionId .getSubPathAttach3());
        assertEquals("Filepath4",  assignmentSubmitForSubmissionId .getSubPathAttach4());
        assertEquals("Filepath5",  assignmentSubmitForSubmissionId .getSubPathAttach5());
    }

    @Test
    @Order(16)
    public void testGetSubmissionsByAssignmentId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/assignmentsubmission/" + 44)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<AssignmentSubmitDTO> assignmentSubmitDtOList = obj.readValue(jsonResponse,
                new TypeReference<List<AssignmentSubmitDTO>>() {
                });
        AssignmentSubmitDTO assignmentSubmitForSubmissionId = null;
        for(AssignmentSubmitDTO assignmentSubmitDTO : assignmentSubmitDtOList){
            if(submissionId.equals(assignmentSubmitDTO.getSubmissionId())){
                assignmentSubmitForSubmissionId = assignmentSubmitDTO;
                break;
            }
        }
        assertNotNull(assignmentSubmitForSubmissionId, "assignmentSubmitForSubmissionId is null");
        assertEquals(44,  assignmentSubmitForSubmissionId .getAssignmentId());
        assertEquals("U06",  assignmentSubmitForSubmissionId .getUserId());
        assertEquals("Submission of data science assignment",  assignmentSubmitForSubmissionId .getSubDesc());
        assertEquals("Submission of project",  assignmentSubmitForSubmissionId .getSubComments());
    }

    @Test
    @Order(17)
    public void testGetGradesByProgramId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/lms/assignmentsubmission/program/" + 2)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<AssignmentSubmitDTO> assignmentSubmitDtOList = obj.readValue(jsonResponse,
                new TypeReference<List<AssignmentSubmitDTO>>() {
                });
        AssignmentSubmitDTO assignmentSubmitForSubmissionId = null;
        for(AssignmentSubmitDTO assignmentSubmitDTO : assignmentSubmitDtOList){
            if(submissionId.equals(assignmentSubmitDTO.getSubmissionId())){
                assignmentSubmitForSubmissionId = assignmentSubmitDTO;
                break;
            }
        }
        assertNotNull(assignmentSubmitForSubmissionId, "assignmentSubmitForSubmissionId is null");
        assertEquals(44,  assignmentSubmitForSubmissionId .getAssignmentId());
        assertEquals("U06",  assignmentSubmitForSubmissionId .getUserId());
        assertEquals("Submission of data science assignment",  assignmentSubmitForSubmissionId .getSubDesc());
        assertEquals("Submission of project",  assignmentSubmitForSubmissionId .getSubComments());
        assertEquals("Filepath1",  assignmentSubmitForSubmissionId .getSubPathAttach1());
        assertEquals("Filepath2",  assignmentSubmitForSubmissionId .getSubPathAttach2());
        assertEquals("Filepath3",  assignmentSubmitForSubmissionId .getSubPathAttach3());
        assertEquals("Filepath4",  assignmentSubmitForSubmissionId .getSubPathAttach4());
        assertEquals("Filepath5",  assignmentSubmitForSubmissionId .getSubPathAttach5());
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void testDeleteSubmission() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(delete("/lms/assignmentsubmission/" + submissionId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
