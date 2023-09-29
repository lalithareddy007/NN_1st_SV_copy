package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.WithMockAdmin;
import com.numpyninja.lms.config.WithMockAdminStaff;
import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.services.AssignmentService;
import com.numpyninja.lms.services.AssignmentSubmitService;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(AssignmentController.class)
@WithMockUser
class AssignmentControllerTest extends AbstractTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssignmentService assignmentService;

    @MockBean
    private AssignmentSubmitService assignmentSubmitService;

    @Autowired
    private ObjectMapper objectMapper;

    private AssignmentDto mockAssignmentDto;

    @BeforeEach
    public void setup() {
        setMockAssignmentAndDto();
    }

    private void setMockAssignmentAndDto() {
        String sDate = "05/25/2022";
        Date dueDate = null;
        try {
            dueDate = new SimpleDateFormat("dd/mm/yyyy").parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mockAssignmentDto = new AssignmentDto(1L, "Test Assignment",
                "Junit test", "practice", dueDate, "Filepath1",
                "Filepath2", "Filepath3", "Filepath4",
                "Filepath5", 1, "U02", "U01");
    }

    @DisplayName("test for creating a new assignment")
    @WithMockAdminStaff
    @Test
    void testCreateAssignment() throws Exception {
        //given
        given(assignmentService.createAssignment(ArgumentMatchers.any(AssignmentDto.class)))
                .willAnswer((i) -> i.getArgument(0));

        //when
        ResultActions response = mockMvc.perform(post("/assignments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockAssignmentDto)));

        //then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.assignmentId", is(mockAssignmentDto.getAssignmentId()), Long.class))
                .andExpect(jsonPath("$.assignmentName", is(mockAssignmentDto.getAssignmentName())));
    }

    @DisplayName("test for updating an assignment")
    @Test
    @WithMockAdmin
    void testUpdateAssignment() throws Exception {
        //given
        Long assignmentId = 1L;
        AssignmentDto updatedAssignmentDto = mockAssignmentDto;
        updatedAssignmentDto.setAssignmentName("New Test Assignment");
        given(assignmentService.updateAssignment(ArgumentMatchers.any(AssignmentDto.class)
                , ArgumentMatchers.any(Long.class))).willReturn(updatedAssignmentDto);

        //when
        ResultActions response = mockMvc.perform(put("/assignments/{id}", assignmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAssignmentDto)));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.assignmentId", is(updatedAssignmentDto.getAssignmentId()), Long.class))
                .andExpect(jsonPath("$.assignmentName", is(updatedAssignmentDto.getAssignmentName())));
    }

    @DisplayName("test for deleting an assignment")
    @Test
    @WithMockAdmin
    void testDeleteAssignment() throws Exception {
        //given
        Long assignmentId = 1L;
        willDoNothing().given(assignmentService).deleteAssignment(assignmentId);

        //when
        ResultActions response = mockMvc.perform(delete("/assignments/{id}", assignmentId));

        //then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("test to get all the assignments")
    @Test
    void testGetAllAssignments() throws Exception {
        //given
        AssignmentDto mockAssignmentDto2 = mockAssignmentDto;
        mockAssignmentDto2.setAssignmentId(2L);
        mockAssignmentDto2.setAssignmentName("Test Assignment2");
        ArrayList<AssignmentDto> assignmentDtoList = new ArrayList();
        assignmentDtoList.add(mockAssignmentDto);
        assignmentDtoList.add(mockAssignmentDto2);
        given(assignmentService.getAllAssignments()).willReturn(assignmentDtoList);

        //when
        ResultActions response = mockMvc.perform(get("/assignments"));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(assignmentDtoList.size())));
    }

    @DisplayName("test to get an assignment by Id")
    @Test
    void testGetAssignmentById() throws Exception {
        //given
        Long assignmentId = 1L;
        given(assignmentService.getAssignmentById(assignmentId))
                .willReturn(mockAssignmentDto);

        //when
        ResultActions response = mockMvc.perform(get("/assignments/{id}", assignmentId));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.assignmentName", is(mockAssignmentDto.getAssignmentName())));

    }


    @DisplayName("test to get assignments for a batch")
    @Test
    void testGetAssignmentsForBatch() throws Exception {
        //given
        Integer batchId = 1;
        AssignmentDto mockAssignmentDto2 = mockAssignmentDto;
        mockAssignmentDto2.setAssignmentId(2L);
        mockAssignmentDto2.setAssignmentName("Test Assignment2");
        ArrayList<AssignmentDto> assignmentDtoList = new ArrayList();
        assignmentDtoList.add(mockAssignmentDto);
        assignmentDtoList.add(mockAssignmentDto2);
        given(assignmentService.getAssignmentsForBatch(batchId))
                .willReturn(assignmentDtoList);

        //when
        ResultActions response = mockMvc.perform(get("/assignments/batch/{batchId}", batchId));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(assignmentDtoList.size())));
    }
}
