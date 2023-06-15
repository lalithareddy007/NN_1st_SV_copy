package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.services.ProgramServices;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
@WithMockUser
@WebMvcTest(value = ProgramController.class)
public class ProgramControllerTest extends AbstractTestController {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgramServices programServices;

    @Autowired
    private ObjectMapper objectMapper;
    private ProgramDTO programDTO;
    private List<ProgramDTO> programList;

    @BeforeEach
    public void setup() {
        setMockProgramWithDto();
    }

    private void setMockProgramWithDto() {
        programDTO = new ProgramDTO(1L, "Java", "Java Description", "Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        programList = new ArrayList<ProgramDTO>();
    }

    @DisplayName("Test for getting all program")
    @Test
    @SneakyThrows
    void testGetPrograms() {
        ProgramDTO programDTO2 = programDTO;
        programDTO2.setProgramId(2L);
        programDTO2.setProgramName("Java 2");
        programDTO2.setProgramDescription("Java 2 Programming");
        programDTO2.setProgramStatus("Active");
        programDTO2.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        programDTO2.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));

        programList.add(programDTO);
        programList.add(programDTO2);
        when(programServices.getAllPrograms()).thenReturn((List<ProgramDTO>) programList);
        ResultActions resultActions = mockMvc.perform(get("/allPrograms"));
        resultActions.andExpectAll(status().isOk(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(programList)),
                MockMvcResultMatchers.jsonPath("$", hasSize(((List<ProgramDTO>) programList).size())),
                MockMvcResultMatchers.jsonPath("$[0].programName", equalTo(programDTO.getProgramName())),
                MockMvcResultMatchers.jsonPath("$[1].programName", equalTo(programDTO2.getProgramName())));

        verify(programServices).getAllPrograms();


    }

    @DisplayName("Test for getting Program by Id")
    @Test
    @SneakyThrows
    public void testGetProgramById() {
        Long programId = 1L;
        given(programServices.getProgramsById(programId)).willReturn(programDTO);
        ResultActions resultActions = mockMvc.perform(get("/programs/{programId}", programId));
        resultActions.andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.programName", equalTo(programDTO.getProgramName())));

    }

    @DisplayName("Test for Creating a Program")
    @Test
    @WithMockAdmin
    @SneakyThrows
    public void testCreateProgram() {
        given(programServices.createAndSaveProgram(ArgumentMatchers.any(ProgramDTO.class)))
                .willAnswer((i) -> i.getArgument(0));
        ResultActions resultActions = mockMvc.perform(post("/saveprogram/")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(programDTO)));

        resultActions.andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.programId", equalTo(programDTO.getProgramId()), Long.class))
                .andExpect((ResultMatcher) jsonPath("$.programName", equalTo(programDTO.getProgramName())));
    }

    @DisplayName("Test for Updating Program by ProgramId")
    @Test
    @SneakyThrows
    public void testUpdateProgramByProgramId() {
        Long programId = 1L;
        ProgramDTO updateProgramDTO = programDTO;
        updateProgramDTO.setProgramName("update program");
        given(programServices.updateProgramById(any(Long.class), any(ProgramDTO.class)))
                .willReturn(updateProgramDTO);
        ResultActions resultActions = mockMvc.perform(put("/putprogram/{programId}", programId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateProgramDTO)));
        resultActions.andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.programId", equalTo(updateProgramDTO.getProgramId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.programName", equalTo(
                        updateProgramDTO.getProgramName())));

    }

    @DisplayName("Test for Updating Program by ProgramName")
    @Test
    @SneakyThrows
    public void testUpdateProgramByProgramName() {
        String programName = "Java Update";
        ProgramDTO updateProgramDTO = programDTO;
        updateProgramDTO.setProgramDescription("Update Desc");
        given(programServices.updateProgramByName(any(String.class), any(ProgramDTO.class)))
                .willReturn(updateProgramDTO);
        ResultActions resultActions = mockMvc.perform(put("/program/{programName}", programName)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateProgramDTO)));
        resultActions.andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.programName", equalTo(updateProgramDTO.getProgramName()), String.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.programDescription", equalTo(
                        updateProgramDTO.getProgramDescription())));

    }

    @DisplayName("Test for Delete Program by ProgramId")
    @Test
    @SneakyThrows
    public void testDeleteProgramByProgramId() {
        Long programId = 2L;
        given(programServices.deleteByProgramId(programId)).willReturn(true);
        ResultActions resultActions = mockMvc.perform(delete("/deletebyprogid/{programId}", programId));
        resultActions.andExpect(status().isOk()).andDo(print());
        verify(programServices).deleteByProgramId(programId);

    }

    @DisplayName("Test for Delete Program by ProgramName")
    @Test
    @SneakyThrows
    public void testDeleteProgramByProgramName() {
        String programName = "Java Delete";
        given(programServices.deleteByProgramName(programName)).willReturn(true);
        ResultActions resultActions = mockMvc.perform(delete("/deletebyprogname/{programName}", programName));
        resultActions.andExpect(status().isOk()).andDo(print());
        verify(programServices).deleteByProgramName(programName);

    }


}
