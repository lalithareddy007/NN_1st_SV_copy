package com.numpyninja.lms.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.*;
import com.numpyninja.lms.dto.BatchDTO;
import com.numpyninja.lms.services.ProgBatchServices;
import com.numpyninja.lms.services.ProgramServices;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProgBatchController.class)
@WithMockUser  // to overcome Login
public class ProgBatchControllerTest extends AbstractTestController {

    @Autowired
    private MockMvc mockMvc;    //MockMVC - WebLayer unit Testing without starting the server

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProgBatchServices batchService;

    @MockBean
    private ProgramServices programService;

    private static List<BatchDTO> listOfBatches;

    @BeforeAll
    public static void setData() {
        long programId1 = 1;
        long programId2 = 2;
        String prg1Desc = "SDET";
        String prg2Desc = "Datascience";

        BatchDTO batchDTO1 = new BatchDTO(1, "01", "SDET BATCH 01", "In Active", 6, programId1, prg1Desc);
        BatchDTO batchDTO2 = new BatchDTO(2, "02", "SDET BATCH 02", "Active", 4, programId1, prg1Desc);
        BatchDTO batchDTO3 = new BatchDTO(3, "01", "DataScience 01", "Active", 6, programId2, prg2Desc);
        listOfBatches = Arrays.asList(batchDTO1, batchDTO2, batchDTO3);
        System.out.println("List size :" + listOfBatches.size());
    }


    @Test
    public void givenBatchList_WhenGetAllBatches_ThenReturnBatchesList() throws Exception {
        // given - BDDMockito.given()
        given(batchService.getAllBatches()).willReturn(listOfBatches);
        mockMvc.perform(MockMvcRequestBuilders.get("/batches").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }


    // positive scenario - GET batch by valid batchId
    @Test
    public void givenBatchId_whenGetBatchById_thenReturnBatch() throws Exception {
        BatchDTO batchDTO2 = listOfBatches.get(1);

        given(batchService.findBatchById(batchDTO2.getBatchId())).willReturn(batchDTO2);

        System.out.println(batchDTO2.toString());
        //When
        ResultActions response = mockMvc.perform(get("/batches/batchId/{batchId}", batchDTO2.getBatchId())
                .contentType(MediaType.APPLICATION_JSON));

        //Then
        response.andDo(print()).andExpect(status().isOk());
        System.out.println("response : " + response.toString());
        response.andExpect(jsonPath("batchId", is(batchDTO2.getBatchId())))
                .andExpect(jsonPath("batchName", is(batchDTO2.getBatchName())));
    }


    /* getBatchByName */
    @Test
    public void givenBatchName_whenGetBatchByName_thenReturnBatches() throws Exception {
        String batchName = "01";
        List<BatchDTO> batch01List = new ArrayList<BatchDTO>();
        batch01List.add(listOfBatches.get(0));
        batch01List.add(listOfBatches.get(2));

        given(batchService.findByProgramBatchName(batchName)).willReturn(batch01List);

        //when
        ResultActions response = mockMvc.perform(get("/batches/batchName/{batchName}", batchName)
                .contentType(MediaType.APPLICATION_JSON));

        //Then
        response.andDo(print()).andExpect(status().isOk());
        response.andExpect(jsonPath("$", hasSize(2)));
    }


    /* getBatchByProgram */
    @Test
    public void givenProgramId_whenGetBatchByProgram_thenReturnBatchches() throws Exception {
        long programId = 1;
        List<BatchDTO> batchList = new ArrayList<BatchDTO>();
        batchList.add(listOfBatches.get(0));
        batchList.add(listOfBatches.get(1));

        given(batchService.findBatchByProgramId(programId)).willReturn(batchList);

        //when
        ResultActions response = mockMvc.perform(get("/batches/program/{programId}", programId)
                .contentType(MediaType.APPLICATION_JSON));

        //Then
        response.andDo(print()).andExpect(status().isOk());
        response.andExpect(jsonPath("$", hasSize(2)));
    }


    @Test
    @WithMockAdminStaff
    //if we dont provide annotation we need to specify like this on every test
    // @WithMockUser(username="tim@gmail.com",roles={"STAFF","ADMIN"})
    public void givenBatch_whenCreateBatch_thenReturnSavedBatch() throws Exception {
        BatchDTO batchDTO3 = listOfBatches.get(2);
        batchDTO3.setBatchName("DataScience1");
        given(batchService.createBatch(org.mockito.ArgumentMatchers.any())).willReturn(batchDTO3);   // it works for this code only

        ResultActions response = mockMvc.perform(post("/batches").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchDTO3)));
        response.andDo(print()).andExpect(status().isCreated());
//        System.out.println("response : " + response.toString());
        response.andExpect(jsonPath("$.batchId", is(batchDTO3.getBatchId())))
                .andExpect(jsonPath("$.batchName", is(batchDTO3.getBatchName())));
    }

    @Test @WithMockStaffStudent
    public void givenBatch_whenCreateBatch_thenReturnReturn403_forbidden() throws Exception {
        BatchDTO batchDTO3 = listOfBatches.get(2);
        batchDTO3.setBatchName("DataScience1");
        given(batchService.createBatch(org.mockito.ArgumentMatchers.any())).willReturn(batchDTO3);   // it works for this code only

        ResultActions response = mockMvc.perform(post("/batches").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(batchDTO3)));
        response.andDo(print()).andExpect(status().isForbidden());

    }

    @Test
    @WithMockAdmin
    public void givenUpdatedBatch_whenUpdateBatch_thenReturnUpdateBatchObject_Role_Admin() throws Exception {
        BatchDTO updateDetailDTO = new BatchDTO(1, "SDET1", "SDET BATCH 01 Updation", "Active", 8, (long) 1, "SDET");

        Integer batchId = 1;

        given(batchService.updateBatch(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).willReturn(updateDetailDTO);   // it works for this code only

        ResultActions response = mockMvc.perform(put("/batches/{batchId}", batchId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetailDTO)));

        response.andDo(print()).andExpect(status().isOk());
        System.out.println("response : " + response.toString());
        response.andExpect(jsonPath("batchId", is(updateDetailDTO.getBatchId())))
                .andExpect(jsonPath("batchName", is(updateDetailDTO.getBatchName())))
                .andExpect(jsonPath("batchDescription", is(updateDetailDTO.getBatchDescription())));
    }

    @Test @WithMockStudent   // 403 forbidden
    public void givenUpdatedBatch_Role_Student_whenUpdateBatch_thenReturn403_forbidden_() throws Exception {
        BatchDTO updateDetailDTO = new BatchDTO(1, "SDET1", "SDET BATCH 01 Updation", "Active", 8, (long) 1, "SDET");

        Integer batchId = 1;

        given(batchService.updateBatch(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any())).willReturn(updateDetailDTO);   // it works for this code only

        ResultActions response = mockMvc.perform(put("/batches/{batchId}", batchId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDetailDTO)));

        response.andDo(print()).andExpect(status().isForbidden());

    }

    @Test @WithMockAdmin
    public void givenBatchId_whenDeleteBatch_thenReturn200() throws Exception {
        Integer batchId = 2;

        // given
        BDDMockito.willDoNothing().given(batchService).deleteProgramBatch(batchId);

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/batches/{batchId}", batchId));

        // then - verify the output
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @Test @WithMockStaff
    public void givenBatchId_Role_Staff_whenDeleteBatch_thenReturn403Forbidden() throws Exception {
        Integer batchId = 2;

        // given
        BDDMockito.willDoNothing().given(batchService).deleteProgramBatch(batchId);

        // when -  action or the behaviour that we are going test
        ResultActions response = mockMvc.perform(delete("/batches/{batchId}", batchId));

        // then - verify the output
        response.andExpect(status().isForbidden())
                .andDo(print());
    }
}


