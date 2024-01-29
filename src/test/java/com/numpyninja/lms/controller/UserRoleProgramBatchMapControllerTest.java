package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.WithMockAdmin;
import com.numpyninja.lms.config.WithMockStaffStudent;
import com.numpyninja.lms.dto.UserRoleProgramBatchMapDto;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import com.numpyninja.lms.services.UserRoleProgramBatchMapService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WithMockUser
@WebMvcTest(value = UserRoleProgramBatchMapController.class)
public class UserRoleProgramBatchMapControllerTest extends AbstractTestController{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRoleProgramBatchMapService userRoleProgramBatchMapService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRoleProgramBatchMapDto mockUserRoleProgramBatchMapDto;

    @BeforeEach
    public void setup() {
        setMockProgramWithDto();
    }

    private void setMockProgramWithDto() {

        mockUserRoleProgramBatchMapDto = new UserRoleProgramBatchMapDto("U30","R03",
                                    1L,1,"Active");

    }

    @DisplayName("test to get Assigned Program/Batch(es) of All Users")
    @WithMockAdmin
    @Test
    void testGetAllWithAdmin() throws Exception {
        //given
        UserRoleProgramBatchMapDto mockUserRoleProgramBatchMapDto2 = new UserRoleProgramBatchMapDto(
                            "U40","R02",2L,2,"Active");
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtoList = new ArrayList<>();
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto);
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto2);
        given(userRoleProgramBatchMapService.getAllUserRoleProgramBatchMaps())
                                                    .willReturn(userRoleProgramBatchMapDtoList);

        //when
        ResultActions response = mockMvc.perform(get("/userRoleProgramBatchMap"));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(userRoleProgramBatchMapDtoList.size())));
    }


    @DisplayName("test to get Assigned Program/Batch(es) of All Users")
    @WithMockStaffStudent
    @Test
    void testGetAllWithStaffStudent() throws Exception {
        //given
        UserRoleProgramBatchMapDto mockUserRoleProgramBatchMapDto2 = new UserRoleProgramBatchMapDto(
                "U40","R02",2L,2,"Active");
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtoList = new ArrayList<>();
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto);
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto2);
        given(userRoleProgramBatchMapService.getAllUserRoleProgramBatchMaps())
                .willReturn(userRoleProgramBatchMapDtoList);

        //when
        ResultActions response = mockMvc.perform(get("/userRoleProgramBatchMap"));

        //then
        response.andExpect(status().isForbidden());
    }

    @DisplayName("test to get Assigned Program/Batch(es) of a User By UserId")
    @WithMockAdmin
    @Test
    void testGetByIdWithAdmin() throws Exception {
       //given
        UserRoleProgramBatchMapDto mockUserRoleProgramBatchMapDto2 = new UserRoleProgramBatchMapDto(
                "U30","R02",2L,2,"Active");
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtoList = new ArrayList<>();
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto);
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto2);

        String userId = "U30";
        given(userRoleProgramBatchMapService.getByUserId(userId))
                .willReturn(userRoleProgramBatchMapDtoList);

        //when
        ResultActions response = mockMvc.perform(get("/userRoleProgramBatchMap/{userId}",userId));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(userRoleProgramBatchMapDtoList.size())));
    }


    @DisplayName("test to get Assigned Program/Batch(es) of a User By UserId")
    @WithMockStaffStudent
    @Test
    void testGetByIdWithStaffStudent() throws Exception {
        //given
        UserRoleProgramBatchMapDto mockUserRoleProgramBatchMapDto2 = new UserRoleProgramBatchMapDto(
                "U30","R02",2L,2,"Active");
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtoList = new ArrayList<>();
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto);
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto2);

        String userId = "U30";
        given(userRoleProgramBatchMapService.getByUserId(userId))
                .willReturn(userRoleProgramBatchMapDtoList);

        //when
        ResultActions response = mockMvc.perform(get("/userRoleProgramBatchMap/{userId}",userId));

        //then
        response.andExpect(status().isForbidden());
    }

    @DisplayName("test to delete all Assigned Program/Batch(es) of a User By UserId")
    @WithMockAdmin
    @Test
    void testdeleteAllProgramBatchesAssignedToAUserWithAdmin() throws Exception {
        //given
        String userId = "U30";
        willDoNothing().given(userRoleProgramBatchMapService).deleteAllByUserId(userId);

        //when
        ResultActions response = mockMvc.perform(delete("/userRoleProgramBatchMap/deleteAll/{userId}",userId));

        //then
        response.andExpect(status().isOk())
                .andDo(print());

    }


    @DisplayName("test to delete all Assigned Program/Batch(es) of a User By UserId")
    @WithMockStaffStudent
    @Test
    void testdeleteAllProgramBatchesAssignedToAUserWithStaffStudent() throws Exception {
        //given
        String userId = "U30";
        willDoNothing().given(userRoleProgramBatchMapService).deleteAllByUserId(userId);

        //when
        ResultActions response = mockMvc.perform(delete("/userRoleProgramBatchMap/deleteAll/{userId}",userId));

        //then
        response.andExpect(status().isForbidden());

    }


}
