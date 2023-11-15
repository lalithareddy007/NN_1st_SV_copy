package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.WithMockAdmin;
import com.numpyninja.lms.config.WithMockAdminStaff;
import com.numpyninja.lms.config.WithMockStaff;
import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.services.AssignmentSubmitService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WithMockUser
@WebMvcTest(AssignmentSubmitController.class)
public class AssignmentSubmitControllerTest extends AbstractTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignmentSubmitService assignmentSubmitService;

    private AssignmentSubmitDTO mockAssignmentSubmitDTO1, mockAssignmentSubmitDTO2,
            mockAssignmentSubmitDTO3, mockAssignmentSubmitDTO4;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    private void setMockAssignmentSubmitDTO() {

        Timestamp timestamp1 = Timestamp.valueOf(LocalDateTime.now());

        mockAssignmentSubmitDTO1 = new AssignmentSubmitDTO(4L, 2L, "U04", "Selenium assignment Submission",
                "First submission", "Filepath1", "Filepath2", "Filepath3", "Filepath4", "Filepath5",
                timestamp1, null, null, -1);
        mockAssignmentSubmitDTO2 = new AssignmentSubmitDTO(3L, 1L, "U04", "SQL assignment Submission",
                "First submission", "Filepath1", "Filepath2", "Filepath3", "Filepath4", "Filepath5",
                timestamp1, null, null, -1);
        mockAssignmentSubmitDTO3 = new AssignmentSubmitDTO(8L, 1L, "U05", "SQL assignment Submission",
                "First submission", "Filepath1", "Filepath2", "Filepath3", "Filepath4", "Filepath5",
                timestamp1, "U04", null, -1);
        mockAssignmentSubmitDTO4 = new AssignmentSubmitDTO(8L, 1L, "U05", "SQL assignment Submission",
                "Submission graded", "Filepath1", "Filepath2", "Filepath3", "Filepath4", "Filepath5",
                timestamp1, "U04", timestamp1, 80);


    }

    @DisplayName("Test to get All Submissions for a given Student Id")
    @Test
    @SneakyThrows
    public void testGetSubmissionsByUserID() {
        String userId = "U04";
        List<AssignmentSubmitDTO> mockAssignmentSubmitDTOsList = new ArrayList<>();
        mockAssignmentSubmitDTOsList.add(mockAssignmentSubmitDTO1);
        mockAssignmentSubmitDTOsList.add(mockAssignmentSubmitDTO2);
        when(assignmentSubmitService.getSubmissionsByUserID(userId)).thenReturn(mockAssignmentSubmitDTOsList);

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission/student/{userID}", userId));

        resultActions.andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockAssignmentSubmitDTOsList)))
                .andExpect(jsonPath("$", hasSize(mockAssignmentSubmitDTOsList.size())))
                .andExpect(jsonPath("$[0].userId", equalTo(mockAssignmentSubmitDTO1.getUserId())))
                .andExpect(jsonPath("$[1].userId", equalTo(mockAssignmentSubmitDTO2.getUserId())))
                .andDo(print());
        assert (mockAssignmentSubmitDTOsList.get(0).getUserId().equals(userId));
        assert (mockAssignmentSubmitDTOsList.get(1).getUserId().equals(userId));
        assert (!mockAssignmentSubmitDTOsList.contains(mockAssignmentSubmitDTO3));
        verify(assignmentSubmitService).getSubmissionsByUserID(userId);

    }

    @Test
    @SneakyThrows
    @DisplayName("Test to get Submissions for a Batch")
    public void testgetSubmissionsByBatch() {
        Integer batchId = 1;
        List<AssignmentSubmitDTO> assignmentSubmitDTOList = new ArrayList<>();
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO1);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO2);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO3);
        when(assignmentSubmitService.getSubmissionsByBatch(batchId)).thenReturn(assignmentSubmitDTOList);

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission/studentbatch/{batchid}", batchId));
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(assignmentSubmitDTOList.size())));
        verify(assignmentSubmitService).getSubmissionsByBatch(batchId);

    }

    @Test
    @SneakyThrows
    @DisplayName("Test for Get Grades if Assignment Id is not found")
    public void testGetGradesByAssignmentId_notfound() {

        Long assignmentId = 1L;
        String message = "Grades for students Assignment Id" + assignmentId + " not found";
        List<AssignmentSubmitDTO> assignmentSubmitDTOList = new ArrayList<>();
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO1);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO2);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO3);
        when(assignmentSubmitService.getGradesByAssinmentId(ArgumentMatchers.any(Long.class))).thenThrow(new ResourceNotFoundException(message));

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission/getGrades/{assignmentId}", assignmentId));
        resultActions.andExpectAll(status().isNotFound(),
                        jsonPath("$.message").value(message))
                .andDo(print());
        verify(assignmentSubmitService).getGradesByAssinmentId(ArgumentMatchers.any(Long.class));
    }

    @Test
    @SneakyThrows
    @DisplayName("Test to Get Grades by Student Id")
    public void testGetGradesByStudentId() {

        String studentId = "U04";
        List<AssignmentSubmitDTO> assignmentSubmitDTOList = new ArrayList<>();
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO1);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO2);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO3);
        when(assignmentSubmitService.getGradesByStudentId(studentId)).thenReturn(assignmentSubmitDTOList);

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission/grades/student/{studentId}", studentId));
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(assignmentSubmitDTOList.size())));
        verify(assignmentSubmitService).getGradesByStudentId(studentId);
    }

    @Test
    @SneakyThrows
    @DisplayName("Test for Get Grades if Student Id is not found")
    public void testGetGradesByStudentId_notFound() {

        String studentId = "U08";
        String message = "Grades for student with " + studentId + " not found";
        List<AssignmentSubmitDTO> assignmentSubmitDTOList = new ArrayList<>();
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO1);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO2);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO3);
        when(assignmentSubmitService.getGradesByStudentId(ArgumentMatchers.any(String.class))).thenThrow(new ResourceNotFoundException(message));

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission/grades/student/{studentId}", studentId));
        resultActions.andExpectAll(status().isNotFound(),
                        jsonPath("$.message").value(message))
                .andDo(print());
        verify(assignmentSubmitService).getGradesByStudentId(ArgumentMatchers.any(String.class));
    }


    @Test
    @SneakyThrows
    @DisplayName("Test for get Submissions if BatchID not found")
    public void testgetSubmissionsByBatch_notfound() {

        Integer batchId = 4;
        String message = "Submissions with batch Id not found";
        List<AssignmentSubmitDTO> assignmentSubmitDTOList = new ArrayList<>();
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO1);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO2);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO3);
        when(assignmentSubmitService.getSubmissionsByBatch(ArgumentMatchers.any(Integer.class))).thenThrow(new ResourceNotFoundException(message));

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission/studentbatch/{batchid}", batchId));
        resultActions.andExpectAll(status().isNotFound(),
                        jsonPath("$.message").value(message))
                .andDo(print());
        verify(assignmentSubmitService).getSubmissionsByBatch(ArgumentMatchers.any(Integer.class));
    }

    @Test
    @SneakyThrows
    @WithMockAdminStaff
    @DisplayName("Test to Get All Submissions list")
    public void testGetAllSubmissions() {
        List<AssignmentSubmitDTO> getAllSubmissionsList = new ArrayList<>();
        getAllSubmissionsList.add(mockAssignmentSubmitDTO1);
        getAllSubmissionsList.add(mockAssignmentSubmitDTO2);
        getAllSubmissionsList.add(mockAssignmentSubmitDTO3);

        when(assignmentSubmitService.getAllSubmissions()).thenReturn(getAllSubmissionsList);

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission"));
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(getAllSubmissionsList.size())));
        verify(assignmentSubmitService).getAllSubmissions();

    }

    @Test
    @SneakyThrows
    @WithMockUser
    @DisplayName("Test to Get All Submissions list")
    public void testGetAllSubmissionsByUser() {
        List<AssignmentSubmitDTO> getAllSubmissionsList = new ArrayList<>();
        getAllSubmissionsList.add(mockAssignmentSubmitDTO1);
        getAllSubmissionsList.add(mockAssignmentSubmitDTO2);
        getAllSubmissionsList.add(mockAssignmentSubmitDTO3);

        when(assignmentSubmitService.getAllSubmissions()).thenReturn(getAllSubmissionsList);

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission"));
        resultActions.andExpect(status().isForbidden());

    }

    @Test
    @SneakyThrows
    @DisplayName("Test to Get Grades by AssignmentId")
    public void testGetGradesByAssignmentId() {
        Long assignmentId = 1L;
        List<AssignmentSubmitDTO> assignmentSubmitDTOList = new ArrayList<>();
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO1);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO2);
        assignmentSubmitDTOList.add(mockAssignmentSubmitDTO3);
        when(assignmentSubmitService.getGradesByAssinmentId(assignmentId)).thenReturn(assignmentSubmitDTOList);

        ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission/getGrades/{assignmentId}", assignmentId));
        resultActions.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(assignmentSubmitDTOList.size())));
        verify(assignmentSubmitService).getGradesByAssinmentId(assignmentId);
    }


    @Test
    @SneakyThrows
    @DisplayName("Test for  ResubmitAssignment")
    @WithMockUser
    public void testResubmitAssignment() {
        Long submissionId = 4L;
        AssignmentSubmitDTO updateAssignmentDTO = mockAssignmentSubmitDTO1;
        updateAssignmentDTO.setAssignmentId(5L);
        updateAssignmentDTO.setUserId("U05");
        updateAssignmentDTO.setGrade(100);
        updateAssignmentDTO.setSubDesc("Java-Collection");
        when(assignmentSubmitService.resubmitAssignment(any(AssignmentSubmitDTO.class), eq(submissionId))).thenReturn(mockAssignmentSubmitDTO1);

        ResultActions resultActions = mockMvc.perform(put("/assignmentsubmission/{id}", submissionId).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateAssignmentDTO)));
        resultActions.andExpect(status().isOk()).andDo(print()).
                andExpect(jsonPath("$.submissionId").value(mockAssignmentSubmitDTO1.getSubmissionId())).
                andExpect(jsonPath("$.assignmentId").value(mockAssignmentSubmitDTO1.getAssignmentId())).
                andExpect(jsonPath("$.userId").value(mockAssignmentSubmitDTO1.getUserId())).
                andExpect(jsonPath("$.subDesc").value(mockAssignmentSubmitDTO1.getSubDesc())).
                andExpect(jsonPath("$.grade").value(mockAssignmentSubmitDTO1.getGrade())).
                andExpect(jsonPath("$.gradedBy").value(mockAssignmentSubmitDTO1.getGradedBy())).
                andExpect(jsonPath("$.gradedDateTime").value(mockAssignmentSubmitDTO1.getGradedDateTime())).
                andExpect(jsonPath("$.*", hasSize(14)));

        verify(assignmentSubmitService, times(1)).resubmitAssignment(any(AssignmentSubmitDTO.class), eq(submissionId));

    }

    @Test
    @SneakyThrows
    @DisplayName("Test for  ResubmitAssignment")
    @WithMockAdminStaff
    public void testResubmitAssignmentByAdmin() {
        Long submissionId = 4L;
        AssignmentSubmitDTO updateAssignmentDTO = mockAssignmentSubmitDTO1;
        updateAssignmentDTO.setAssignmentId(5L);
        updateAssignmentDTO.setUserId("U05");
        updateAssignmentDTO.setGrade(100);
        updateAssignmentDTO.setSubDesc("Java-Collection");
        when(assignmentSubmitService.resubmitAssignment(any(AssignmentSubmitDTO.class), eq(submissionId))).thenReturn(mockAssignmentSubmitDTO1);

        ResultActions resultActions = mockMvc.perform(put("/assignmentsubmission/{id}", submissionId).contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateAssignmentDTO)));
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("Test to grade submissions")
    @WithMockStaff
    public void testGradeSubmissions() {
        long submissionId = 8L;
        given(assignmentSubmitService.gradeAssignmentSubmission(any(AssignmentSubmitDTO.class), eq(submissionId)))
                .willReturn(mockAssignmentSubmitDTO4);
        ResultActions resultActions = mockMvc.perform(put("/assignmentsubmission/gradesubmission/{submissionId}", submissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockAssignmentSubmitDTO4)));
        resultActions.andExpect(status().isOk()).andDo(print())
                .andExpect(jsonPath("$.assignmentId").value(mockAssignmentSubmitDTO4.getAssignmentId()))
                .andExpect((jsonPath("$.userId").value(mockAssignmentSubmitDTO4.getUserId())))
                .andExpect(jsonPath("$.grade").value(mockAssignmentSubmitDTO4.getGrade()))
                .andExpect(jsonPath("$.gradedBy").value(mockAssignmentSubmitDTO4.getGradedBy()));

        verify(assignmentSubmitService).gradeAssignmentSubmission(any(AssignmentSubmitDTO.class), eq(submissionId));

    }

    @Test
    @SneakyThrows
    @DisplayName("Test to grade submissions")
    @WithMockAdmin
    public void testGradeSubmissionsByAdmin() {
        long submissionId = 8L;
        given(assignmentSubmitService.gradeAssignmentSubmission(any(AssignmentSubmitDTO.class), eq(submissionId)))
                .willReturn(mockAssignmentSubmitDTO4);
        ResultActions resultActions = mockMvc.perform(put("/assignmentsubmission/gradesubmission/{submissionId}", submissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockAssignmentSubmitDTO4)));
        resultActions.andExpect(status().isForbidden());
    }
}



