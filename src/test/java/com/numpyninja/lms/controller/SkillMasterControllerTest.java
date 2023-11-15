package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.WithMockAdminStaff;
import com.numpyninja.lms.dto.SkillMasterDto;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.services.SkillMasterService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = SkillMasterController.class)
@WithMockUser
public class SkillMasterControllerTest extends AbstractTestController {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkillMasterService skillMasterService;

    private SkillMasterDto mockSkillMasterDto;
    private List<SkillMasterDto> skillMasterDtoList;

    @BeforeEach
    public void setUp() {
        setMockSkillMasterWithDto();
    }

    private void setMockSkillMasterWithDto() {
        mockSkillMasterDto = new SkillMasterDto(1L, "Java", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        skillMasterDtoList = new ArrayList<SkillMasterDto>();
    }

    @Nested
    class GetOperation {

        @DisplayName("test - GetAllSkills - Ok")
        @SneakyThrows
        @Test
        public void testGetAllSkills() {
            //given
            SkillMasterDto mockSkillMasterDto2 = new SkillMasterDto();
            mockSkillMasterDto2.setSkillId(2L);
            mockSkillMasterDto2.setSkillName("SQL");
            mockSkillMasterDto2.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
            mockSkillMasterDto2.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));

            skillMasterDtoList.add(mockSkillMasterDto);
            skillMasterDtoList.add(mockSkillMasterDto2);

            when(skillMasterService.getAllSkillMaster()).thenReturn(skillMasterDtoList);

            //when
            ResultActions resultActions = mockMvc.perform(get("/allSkillMaster"));

            //then
            resultActions.andExpectAll(status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            content().json(objectMapper.writeValueAsString(skillMasterDtoList)),
                            jsonPath("$", hasSize(skillMasterDtoList.size())),
                            jsonPath("$[0].skillName", equalTo(mockSkillMasterDto.getSkillName())),
                            jsonPath("$[1].skillName", equalTo(mockSkillMasterDto2.getSkillName())))
                    .andDo(print());

            verify(skillMasterService).getAllSkillMaster();
        }

        @DisplayName("test - GetAllSkills - Not Found")
        @SneakyThrows
        @Test
        public void testGetAllSkillsNotFound() {
            //given
            String message = "skillMaster list is not found";
            when(skillMasterService.getAllSkillMaster()).thenThrow(new ResourceNotFoundException(message));

            //when
            ResultActions resultActions = mockMvc.perform(get("/allSkillMaster"));

            //then
            resultActions.andExpectAll(status().isNotFound(),
                    jsonPath("$.message").value(message));

            verify(skillMasterService).getAllSkillMaster();
        }

        @DisplayName("test - GetSkillByName - Ok")
        @SneakyThrows
        @Test
        public void testGetSkillByName() {
            //given
            skillMasterDtoList.add(mockSkillMasterDto);
            String skillName = "Java";
            given(skillMasterService.getSkillMasterByName(skillName)).willReturn(skillMasterDtoList);

            //when
            ResultActions resultActions = mockMvc.perform(get("/skills/{skillMasterName}", skillName));

            //then
            resultActions.andExpectAll(status().isOk(),
                            jsonPath("$", hasSize(1)))
                    .andDo(print());

            verify(skillMasterService).getSkillMasterByName(skillName);
        }

        @DisplayName("test - GetSkillByName - Not Found")
        @SneakyThrows
        @Test
        public void testGetSkillByNameNotFound() {
            //given
            String skillName = "SQL";
            String message = "skill with id SQL not found";
            when(skillMasterService.getSkillMasterByName(skillName)).thenThrow(new ResourceNotFoundException(message));

            //when
            ResultActions resultActions = mockMvc.perform(get("/skills/{skillMasterName}", skillName));

            //then
            resultActions.andExpectAll(status().isNotFound(),
                    jsonPath("$.message").value(message));

            verify(skillMasterService).getSkillMasterByName(skillName);
        }
    }

    @Nested
    class DeleteOperation {

        @DisplayName("test - DeleteSkillById - Ok")
        @SneakyThrows
        @WithMockAdminStaff
        @Test
        public void testDeleteSkillById() {
            //given
            Long skillId = 1L;
            given(skillMasterService.deleteBySkillId(skillId)).willReturn(true);

            //when
            ResultActions resultActions = mockMvc.perform(delete("/deletebySkillId/{skillId}", skillId));

            //then
            resultActions.andExpect(status().isOk());

            verify(skillMasterService).deleteBySkillId(skillId);
        }
        @DisplayName("test - DeleteSkillById - Ok")
        @SneakyThrows
        @WithMockUser
        @Test
        public void testDeleteSkillByIdByUser() {
            //given
            Long skillId = 1L;
            given(skillMasterService.deleteBySkillId(skillId)).willReturn(true);

            //when
            ResultActions resultActions = mockMvc.perform(delete("/deletebySkillId/{skillId}", skillId));

            //then
            resultActions.andExpect(status().isForbidden());
        }

        @DisplayName("test - DeleteSkillById - Not Found")
        @SneakyThrows
        @WithMockAdminStaff
        @Test
        public void testDeleteSkillByIdNotFound() {
            //given
            Long skillId = 3L;
            given(skillMasterService.deleteBySkillId(skillId)).willReturn(false);

            //when
            ResultActions resultActions = mockMvc.perform(delete("/deletebySkillId/{skillId}", skillId));

            //then
            resultActions.andExpect(status().isNotFound());

            verify(skillMasterService).deleteBySkillId(skillId);
        }

    }

    @Nested
    class PostOperation {

        @DisplayName("test - CreateAndSaveSkill")
        @SneakyThrows
        @WithMockAdminStaff
        @Test
        public void testCreateAndSaveSkill() {
            //given
            when(skillMasterService.createAndSaveSkillMaster(any(SkillMasterDto.class))).thenReturn(mockSkillMasterDto);

            //when
            ResultActions resultActions = mockMvc.perform(post("/SaveSkillMaster")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockSkillMasterDto)));

            //then
            resultActions.andExpectAll(status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.skillName", is(mockSkillMasterDto.getSkillName())))
                    .andDo(print());

            verify(skillMasterService).createAndSaveSkillMaster(any(SkillMasterDto.class));
        }
        @DisplayName("test - CreateAndSaveSkill")
        @SneakyThrows
        @WithMockUser
        @Test
        public void testCreateAndSaveSkillByUser() {
            //given
            when(skillMasterService.createAndSaveSkillMaster(any(SkillMasterDto.class))).thenReturn(mockSkillMasterDto);

            //when
            ResultActions resultActions = mockMvc.perform(post("/SaveSkillMaster")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockSkillMasterDto)));

            //then
            resultActions.andExpectAll(status().isForbidden());
        }

    }

    @Nested
    class PutOperation {

        @DisplayName("test - UpdateSkillById - Ok")
        @SneakyThrows
        @WithMockAdminStaff
        @Test
        public void testUpdateSkillById() {

            //given
            Long skillId = 1L;
            SkillMasterDto updatedSkillMasterDto = mockSkillMasterDto;
            updatedSkillMasterDto.setSkillName("New Java");
            when(skillMasterService.updateSkillMasterById(ArgumentMatchers.any(Long.class),
                    ArgumentMatchers.any(SkillMasterDto.class))).thenReturn(updatedSkillMasterDto);

            //when
            ResultActions response = mockMvc.perform(put("/updateSkills/{skillId}", skillId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedSkillMasterDto)));

            //then
            response.andExpectAll(status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.skillName", is(updatedSkillMasterDto.getSkillName())))
                    .andDo(print());

            verify(skillMasterService).updateSkillMasterById(ArgumentMatchers.any(Long.class),
                    ArgumentMatchers.any(SkillMasterDto.class));
        }
        @DisplayName("test - UpdateSkillById - Ok")
        @SneakyThrows
        @WithMockUser
        @Test
        public void testUpdateSkillByIdByUser() {

            //given
            Long skillId = 1L;
            SkillMasterDto updatedSkillMasterDto = mockSkillMasterDto;
            updatedSkillMasterDto.setSkillName("New Java");
            when(skillMasterService.updateSkillMasterById(ArgumentMatchers.any(Long.class),
                    ArgumentMatchers.any(SkillMasterDto.class))).thenReturn(updatedSkillMasterDto);

            //when
            ResultActions response = mockMvc.perform(put("/updateSkills/{skillId}", skillId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedSkillMasterDto)));

            //then
            response.andExpectAll(status().isForbidden());
        }

        @DisplayName("test - UpdateSkillById - Not Found")
        @SneakyThrows
        @WithMockAdminStaff
        @Test
        public void testUpdateSkillByIdNotFound() {

            //given
            Long skillId = 3L;
            String message = "skill with id 3 not found";
            SkillMasterDto updatedSkillMasterDto = mockSkillMasterDto;
            updatedSkillMasterDto.setSkillId(skillId);
            updatedSkillMasterDto.setSkillName("New Java");
            when(skillMasterService.updateSkillMasterById(ArgumentMatchers.any(Long.class),
                    ArgumentMatchers.any(SkillMasterDto.class))).thenThrow(new ResourceNotFoundException(message));

            //when
            ResultActions response = mockMvc.perform(put("/updateSkills/{skillId}", skillId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedSkillMasterDto)));

            //then
            response.andExpectAll(status().isNotFound(),
                            jsonPath("$.message").value(message))
                    .andDo(print());

            verify(skillMasterService).updateSkillMasterById(ArgumentMatchers.any(Long.class),
                    ArgumentMatchers.any(SkillMasterDto.class));
        }
    }
}


