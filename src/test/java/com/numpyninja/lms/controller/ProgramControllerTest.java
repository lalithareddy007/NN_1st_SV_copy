package com.numpyninja.lms.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.dto.ProgramWithUsersDTO;
import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.services.ProgramServices;

import lombok.SneakyThrows;
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
    private ProgramWithUsersDTO programWithUsersDto;
    
    private List<ProgramDTO> programList;
    private List<ProgramWithUsersDTO> programWithUsersList;

    @BeforeEach
    public void setup() {
        setMockProgramWithDto();
    }

    private void setMockProgramWithDto() {
        programDTO = new ProgramDTO(1L, "Java", "Java Description", "Active", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        programList = new ArrayList<ProgramDTO>();
    }
    
    private  void buildProgramWithUsers() {
		UserDto u1 = new UserDto("U01", "Srinivasa", "Ramanujan", " ", 2323232323L, "India", "IST",
				"www.linkedin.com/Ramanujan1234", "MCA", "MBA", "Indian scientist", "H1B","srinivasa.ramanujan@gmail.com");

    	UserDto u2 = new UserDto("U02", "CV", "Raman", " ", 2323232324L, "USA", "EST",
				"www.linkedin.com/Raman1234", "PHD", "MS", "Indian scientist", "Citizen","cv.raman@gmail.com");

    	UserDto u3 = new UserDto("U03", "Homi", "Bhabha", " ", 2323232325L, "India", "IST",
				"www.linkedin.com/Bhabha1234", "Phd", "MS", "Indian scientist", "H1B","homi.bhabha@gmail.com");

    	ProgramWithUsersDTO programWithUsersDto1 = new ProgramWithUsersDTO(1L, 
    			"SDET", 
    			"SDET Program", 
    			"Active", 
    			List.of(u1,u2,u3), 
    			Timestamp.valueOf(LocalDateTime.now()), 
    			Timestamp.valueOf(LocalDateTime.now()));
    	
    	ProgramWithUsersDTO programWithUsersDto2 = new ProgramWithUsersDTO(2L, 
    			"DA", 
    			"DA Program", 
    			"Active", 
    			List.of(u1,u2), 
    			Timestamp.valueOf(LocalDateTime.now()), 
    			Timestamp.valueOf(LocalDateTime.now()));
    	
    	programWithUsersList = List.of(programWithUsersDto1, programWithUsersDto2);
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
    
    @DisplayName("Test for getting all programs and users in program")
    @Test
    @SneakyThrows
    public void getProgramsWithUsers() {
    	//Configure mock output data
    	buildProgramWithUsers();
    	//Configure programServices.getAllProgramsWithUsers() to return mock data, when invoked 
    	when(programServices.getAllProgramsWithUsers()).thenReturn(programWithUsersList);
    	//invoke the REST API using mockMvc
    	ResultActions resultActions = mockMvc.perform(get("/allProgramsWithUsers"));
    	//Validate the results received
    	
    	resultActions.andExpectAll(status().isOk(),
    	MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
    	MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(programWithUsersList)),
    	MockMvcResultMatchers.jsonPath("$", hasSize(programWithUsersList.size())),
    	MockMvcResultMatchers.jsonPath("$[0].programName", equalTo(programWithUsersList.get(0).getProgramName())),
    	MockMvcResultMatchers.jsonPath("$[1].programName", equalTo(programWithUsersList.get(1).getProgramName())));		
    			
    	//Verifies the request is run only once
    	verify(programServices).getAllProgramsWithUsers();
    
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
