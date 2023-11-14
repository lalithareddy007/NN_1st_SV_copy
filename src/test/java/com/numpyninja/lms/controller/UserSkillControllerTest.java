package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.WithMockAdmin;
import com.numpyninja.lms.config.WithMockStaff;
import com.numpyninja.lms.dto.UserSkillDTO;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserSkillMapper;
import com.numpyninja.lms.services.UserSkillService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserSkillController.class)
@WithMockUser
public class UserSkillControllerTest extends AbstractTestController {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserSkillService userSkillService;

    @MockBean
    private UserSkillMapper userSkillMapper;
    @Autowired
    private ObjectMapper objectMapper;
    private UserSkillDTO userSkillDTO;
    private List<UserSkillDTO> userSkillList;

    @BeforeEach
    public void setup() {
        setMockUserSkillWithDto();
    }

    private void setMockUserSkillWithDto() {
        UserSkillDTO userSkillDTO1 = new UserSkillDTO("US02", "U02", 3, "Java", 9);
        UserSkillDTO userSkillDTO3 = new UserSkillDTO("US03", "U02", 3, "Java", 9);
        UserSkillDTO userSkillDTO2 = new UserSkillDTO("US04", "U03", 2, "Python", 12);
        userSkillList = Arrays.asList(userSkillDTO1,userSkillDTO2, userSkillDTO3);
    }

    @DisplayName("Test for getting all UserSkills")
    @Test
    @SneakyThrows
    @WithMockAdmin
    void getAllUserSkillTest() {
        given(userSkillService.getAllUserSkills()).willReturn(userSkillList);
        mockMvc.perform(get("/userSkill")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
        verify(userSkillService).getAllUserSkills();
    }
    @DisplayName("Test for getting all UserSkills")
    @Test
    @SneakyThrows
    @WithMockUser
    void getAllUserSkillTestByUser() {
            given(userSkillService.getAllUserSkills()).willReturn(userSkillList);
            mockMvc.perform(get("/userSkill")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpectAll(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        verify(userSkillService).getAllUserSkills();
    }

    @DisplayName("Test for getting all UserSkills for particular User")
    @Test
    @SneakyThrows
    @WithMockStaff
    void getUserSkillForUserTest() {
        List<UserSkillDTO> expectedUserSkills = userSkillList.stream().filter(userSkillDTO->userSkillDTO.getUserId().equals("U02")).collect(Collectors.toList());
        String userId = "U02";

        given(userSkillService.getUserSkillForUser(userId)).willReturn(expectedUserSkills);
        ResultActions resultActions = mockMvc.perform(get("/userSkill/user/{userId}", userId));
        resultActions.andExpectAll(status().isOk()).andExpect(jsonPath("$",hasSize(2)));
        verify(userSkillService).getUserSkillForUser(userId);
    }
    @DisplayName("Test for getting all UserSkills for particular User")
    @Test
    @SneakyThrows
    @WithMockUser
    void getUserSkillForUserTestByUser() {
        List<UserSkillDTO> expectedUserSkills = userSkillList.stream().filter(userSkillDTO->userSkillDTO.getUserId().equals("U02")).collect(Collectors.toList());
        String userId = "U02";

        given(userSkillService.getUserSkillForUser(userId)).willReturn(expectedUserSkills);
        ResultActions resultActions = mockMvc.perform(get("/userSkill/user/{userId}", userId));
        resultActions.andExpectAll(status().isOk()).andExpect(jsonPath("$",hasSize(2)));
        verify(userSkillService).getUserSkillForUser(userId);
    }
    @DisplayName("Test for getting all UserSkills for particular User")
    @Test
    @SneakyThrows
    @WithMockStaff
    void getUserSkillForUserTest_WhenUserIdNotFound() {
        String userId = "U013";
        String message = "User not found with Id :" + userId;
        given(userSkillService.getUserSkillForUser(userId)).willThrow(new ResourceNotFoundException(message));
        ResultActions resultActions = mockMvc.perform(get("/userSkill/user/{userId}", userId));
        resultActions.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message",is("User not found with Id :" + userId)))
                .andExpect(jsonPath("$.success", is(false)));
        verify(userSkillService).getUserSkillForUser(userId);
    }

    @DisplayName("Test for Creating a UserSkill")
    @Test
    @SneakyThrows
    @WithMockAdmin
    public void createUserSkillTest() {
        //given
        UserSkillDTO userSkillDTO2 = userSkillList.get(1);
        given(userSkillService.createUserSkill(ArgumentMatchers.any())).willReturn(userSkillDTO2);
        //when
        ResultActions resultActions = mockMvc.perform(post("/userSkill/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userSkillDTO2)));
        //then
        resultActions.andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.userId", equalTo(userSkillDTO2.getUserId()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userSkillId", equalTo(userSkillDTO2.getUserSkillId())));
    }
    @DisplayName("Test for Creating a UserSkill")
    @Test
    @SneakyThrows
    @WithMockUser
    public void createUserSkillTestByUser() {
        //given
        UserSkillDTO userSkillDTO2 = userSkillList.get(1);
        given(userSkillService.createUserSkill(ArgumentMatchers.any())).willReturn(userSkillDTO2);
        //when
        ResultActions resultActions = mockMvc.perform(post("/userSkill/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userSkillDTO2)));
        //then
        resultActions.andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.userId", equalTo(userSkillDTO2.getUserId()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userSkillId", equalTo(userSkillDTO2.getUserSkillId())));
    }

    @DisplayName("Test for Updating UserSkill by UserSKillId")
    @Test
    @SneakyThrows
    @WithMockStaff
    public void updateUserSkillByUserIdTest() {
        //given
        UserSkillDTO updatedUserSkillDTO = new UserSkillDTO("US02", "U02", 2, "Python", 9);
        String userSkillId = "US02";
        given(userSkillService.updateUserSkill(ArgumentMatchers.any(UserSkillDTO.class), ArgumentMatchers.any(String.class)))
                .willReturn(updatedUserSkillDTO);
        //when
        ResultActions resultActions = mockMvc.perform(put("/userSkill/{id}", userSkillId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserSkillDTO)));
        //then
        resultActions.andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", equalTo(updatedUserSkillDTO.getUserId()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userSkillId", equalTo(updatedUserSkillDTO.getUserSkillId())));
    }
    @DisplayName("Test for Updating UserSkill by UserSKillId")
    @Test
    @SneakyThrows
    @WithMockUser
    public void updateUserSkillByUserIdTestByUser() {
        //given
        UserSkillDTO updatedUserSkillDTO = new UserSkillDTO("US02", "U02", 2, "Python", 9);
        String userSkillId = "US02";
        given(userSkillService.updateUserSkill(ArgumentMatchers.any(UserSkillDTO.class), ArgumentMatchers.any(String.class)))
                .willReturn(updatedUserSkillDTO);
        //when
        ResultActions resultActions = mockMvc.perform(put("/userSkill/{id}", userSkillId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserSkillDTO)));
        //then
        resultActions.andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId", equalTo(updatedUserSkillDTO.getUserId()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userSkillId", equalTo(updatedUserSkillDTO.getUserSkillId())));
    }

    /*  @DisplayName("Test - Delete UserSkill By UserId ")
      @SneakyThrows
      @Test
      void deleteUserSkillByUserId() {
          //given
          String userId = "U10";
          willDoNothing().given(userSkillService).deleteUserByUserId(userId);
          //when
          ResultActions resultActions = mockMvc.perform(delete("/userSkill/deleteByUser/{id}", userId));
          //then
          resultActions.andExpect(status().isOk());
          verify(userSkillService).deleteUserByUserId(userId);
      }*/
    @DisplayName("Test - Delete UserSkill By UserSKillId ")
    @SneakyThrows
    @Test
    @WithMockAdmin
    void deleteUserSkillByUserSkillId() {
        //given
        String userSkillId = "US03";
        willDoNothing().given(userSkillService).deleteUserSkillByUserSkillId(userSkillId);
        //when
        ResultActions resultActions = mockMvc.perform(delete("/userSkill/deleteByUserSkillId/{id}", userSkillId));
        //then
        resultActions.andExpect(status().isOk());
        verify(userSkillService).deleteUserSkillByUserSkillId(userSkillId);
    }
    @DisplayName("Test - Delete UserSkill By UserSKillId ")
    @SneakyThrows
    @Test
    @WithMockUser
    void deleteUserSkillByUserSkillIdByUser() {
        //given
        String userSkillId = "US03";
        willDoNothing().given(userSkillService).deleteUserSkillByUserSkillId(userSkillId);
        //when
        ResultActions resultActions = mockMvc.perform(delete("/userSkill/deleteByUserSkillId/{id}", userSkillId));
        //then
        resultActions.andExpect(status().isOk());
        verify(userSkillService).deleteUserSkillByUserSkillId(userSkillId);
    }
}
