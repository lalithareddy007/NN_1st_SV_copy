package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.UserSkillDTO;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(UserSkillController.class)
public class UserSkillControllerTest {
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
        userSkillDTO = new UserSkillDTO("US02", "U02", 3, "Java", 9);
        userSkillList = new ArrayList<UserSkillDTO>();
    }

    @DisplayName("Test for getting all UserSkills")
    @Test
    @SneakyThrows
    void getAllUserSkillTest() {
        //given
        UserSkillDTO userSkillDTO1 = userSkillDTO;
        userSkillDTO1.setUserSkillId("US01");
        userSkillDTO1.setSkillId(2);
        userSkillDTO1.setUserId("U01");
        userSkillDTO1.setSkillName("Python");
        userSkillList.add(userSkillDTO);
        userSkillList.add(userSkillDTO1);
        //when
        when(userSkillService.getAllUserSkills()).thenReturn((List<UserSkillDTO>) userSkillList);
        ResultActions resultActions = mockMvc.perform(get("/userSkill"));
        resultActions.andExpectAll(status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(userSkillList)),
                jsonPath("$", hasSize(((List<UserSkillDTO>) userSkillList).size())),
                jsonPath("$[0].skillName", equalTo(userSkillDTO.getSkillName())),
                jsonPath("$[1].skillName", equalTo(userSkillDTO1.getSkillName())));
        //then
        verify(userSkillService).getAllUserSkills();

    }

    @DisplayName("Test for getting all UserSkills for particular User")
    @Test
    @SneakyThrows
    void getUserSkillForUserTest() {
        //given
        String userId = "U10";
        UserSkillDTO mockDTO = userSkillDTO;
        mockDTO.setUserSkillId("US2");
        mockDTO.setMonths(10);
        userSkillList.add(mockDTO);
        userSkillList.add(userSkillDTO);
        given(userSkillService.getUserSkillForUser(userId)).willReturn(userSkillList);
        //when
        ResultActions resultActions = mockMvc.perform(get("/userSkill/user/{userId}", userId));
        resultActions.andExpectAll(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(userSkillList.size())));
        //then
        verify(userSkillService).getUserSkillForUser(userId);
    }
    @DisplayName("Test for Creating a UserSkill")
    @Test
    @SneakyThrows
    public void createUserSkillTest(){
        //given
        given(userSkillService.createUserSkill(ArgumentMatchers.any(UserSkillDTO.class)))
                .willAnswer((i)->i.getArgument(0));
        //when
        ResultActions resultActions=mockMvc.perform(post("/userSkill/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userSkillDTO)));
        //then
        resultActions.andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.userId",equalTo(userSkillDTO.getUserId()),String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userSkillId",equalTo(userSkillDTO.getUserSkillId())));
    }
    @DisplayName("Test for Updating UserSkill by UserSKillId")
    @Test
    @SneakyThrows
    public void updateUserSkillByUserIdTest(){
        //given
        String userSkillId="U1";
        UserSkillDTO updatedUserSkillDTO=userSkillDTO;
        updatedUserSkillDTO.setUserSkillId("US1");
        given(userSkillService.updateUserSkill(ArgumentMatchers.any(UserSkillDTO.class),ArgumentMatchers.any(String.class)))
                .willReturn(updatedUserSkillDTO);
        //when
        ResultActions resultActions=mockMvc.perform(put("/userSkill/{id}",userSkillId)
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updatedUserSkillDTO)));
        //then
        resultActions.andExpect(status().isOk()).andDo(print())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.userId",equalTo(updatedUserSkillDTO.getUserId()),String.class))
                        .andExpect(MockMvcResultMatchers.jsonPath("$.userSkillId",equalTo(updatedUserSkillDTO.getUserSkillId())));
    }
    @DisplayName("Test - Delete UserSkill By UserId ")
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
    }
    @DisplayName("Test - Delete UserSkill By UserSKillId ")
    @SneakyThrows
    @Test
    void deleteUserSkillByUserSkillId() {
        //given
        String userSkillId = "US10";
        willDoNothing().given(userSkillService).deleteUserSkillByUserSkillId(userSkillId);
        //when
        ResultActions resultActions = mockMvc.perform(delete("/userSkill/deleteByUserSkillId/{id}", userSkillId));
        //then
        resultActions.andExpect(status().isOk());
        verify(userSkillService).deleteUserSkillByUserSkillId(userSkillId);
    }
}
