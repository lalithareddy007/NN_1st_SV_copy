package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.services.AssignmentSubmitService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(AssignmentSubmitController.class)
public class AssignmentSubmitControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AssignmentSubmitService assignmentSubmitService;

        private AssignmentSubmitDTO mockAssignmentSubmitDTO1,mockAssignmentSubmitDTO2,mockAssignmentSubmitDTO3;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        private void setMockAssignmentSubmitDTO(){
            Timestamp timestamp1 = Timestamp.valueOf(LocalDateTime.now());

            mockAssignmentSubmitDTO1 = new AssignmentSubmitDTO(4L,2L,"U04","Selenium assignment Submission",
                   "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                    timestamp1,null,null,-1);
            mockAssignmentSubmitDTO2 = new AssignmentSubmitDTO(3L,1L,"U04","SQL assignment Submission",
                    "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                    timestamp1,null,null,-1);
            mockAssignmentSubmitDTO3 = new AssignmentSubmitDTO(8L,1L,"U05","SQL assignment Submission",
                    "First submission","Filepath1", "Filepath2","Filepath3", "Filepath4","Filepath5",
                    timestamp1,null,null,-1);


        }

        @DisplayName("Test to get all submissions for a given student ID")
        @Test
        @SneakyThrows
        public void testGetSubmissionsByUserID(){
            String userId = "U04";
            List<AssignmentSubmitDTO> mockAssignmentSubmitDTOsList = new ArrayList<>();
            mockAssignmentSubmitDTOsList.add(mockAssignmentSubmitDTO1);
            mockAssignmentSubmitDTOsList.add(mockAssignmentSubmitDTO2);
            when(assignmentSubmitService.getSubmissionsByUserID(userId)).thenReturn(mockAssignmentSubmitDTOsList);

            ResultActions resultActions = mockMvc.perform(get("/assignmentsubmission/student/{userID}",userId));

            resultActions.andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(mockAssignmentSubmitDTOsList)))
                    .andExpect(jsonPath("$",hasSize(mockAssignmentSubmitDTOsList.size())))
                    .andExpect(jsonPath("$[0].userId",equalTo(mockAssignmentSubmitDTO1.getUserId())))
                    .andExpect(jsonPath("$[1].userId",equalTo(mockAssignmentSubmitDTO2.getUserId())))
                    .andDo(print());
            assert(mockAssignmentSubmitDTOsList.get(0).getUserId().equals(userId));
            assert(mockAssignmentSubmitDTOsList.get(1).getUserId().equals(userId));
            assert(!mockAssignmentSubmitDTOsList.contains(mockAssignmentSubmitDTO3));
            verify(assignmentSubmitService).getSubmissionsByUserID(userId);

        }

}
