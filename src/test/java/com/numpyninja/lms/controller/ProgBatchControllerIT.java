package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.BatchDTO;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.repository.ProgBatchRepository;
import com.numpyninja.lms.repository.ProgramRepository;
import com.numpyninja.lms.services.ProgramServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(ProgramController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")

public class ProgBatchControllerIT {
	private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ProgBatchRepository progBatchRepository;
    ObjectMapper obj = new ObjectMapper();

    private String token;

    @BeforeEach
    public void setup() throws Exception {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        final LoginDto loginDto = new LoginDto("John.Matthew@gmail.com", "John123");

        final String  responseBody = obj.writeValueAsString(loginDto);

        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(responseBody))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());

        final JwtResponseDto jwtResponseDto = obj.readValue(mvcResult.getResponse().getContentAsString(),
                JwtResponseDto.class);
        token = jwtResponseDto.getToken();

        assertNotNull(token, "token is null");
    }

    @Test
    public void testGetAllBatches() throws Exception {
    	  final MvcResult mvcResult = mockMvc.perform(get("/lms/batches").contextPath("/lms")
                  .header("Authorization", "Bearer " + token)
                  .contentType("application/json"))
          .andReturn();
    	  
    	  assertEquals(200, mvcResult.getResponse().getStatus());
    	
    }
    
    @Test
    public void testGetBatchById() throws Exception {
    	int batchId=progBatchRepository.findAll().get(0).getBatchId();
        final MvcResult mvcResult = mockMvc.perform(get("/lms/batches/batchId/{batchId}", batchId).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
        .andReturn();
    	
    	 String responseBody = mvcResult.getResponse().getContentAsString();
    	 BatchDTO batchDTO=obj.readValue(responseBody, BatchDTO.class);
         assertEquals(batchId,batchDTO.getBatchId());
    	
    }
    
    @Test
    public void testGetBatchByInvalidId() throws Exception {
    	int batchId=9999;
        final MvcResult mvcResult = mockMvc.perform(get("/lms/batches/batchId/{batchId}", batchId).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
        .andReturn();
        
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();
        System.out.println("message=="+message);

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Batch not found with Id : 9999 ", message);
    	
    }
   
    @Test
   public void testGetBatchByName() throws Exception{
    	
    	String batchName= progBatchRepository.findAll().get(1).getBatchName();
    	final MvcResult mvcResult = mockMvc.perform(get("/lms/batches/batchName/{batchName}", batchName).contextPath("/lms")
               .header("Authorization", "Bearer " + token)
               .contentType("application/json"))
       .andReturn();
	   
	   assertEquals(200, mvcResult.getResponse().getStatus());
	   
	   String responseBody = mvcResult.getResponse().getContentAsString();
	   
  	   List<BatchDTO> batchDTO = obj.readValue(responseBody, new TypeReference<List<BatchDTO>>() {
       });
       BatchDTO firstBatch  = batchDTO.get(0);
      
       assertEquals(1, firstBatch.getProgramId());
       assertEquals("02", firstBatch.getBatchName());
       assertEquals("SDET BATCH 02", firstBatch.getBatchDescription());
       assertEquals(6, firstBatch.getBatchNoOfClasses());
       assertEquals("Active", firstBatch.getBatchStatus());
  }
    
    @Test
    public void testGetBatchByInvalidName() throws Exception{
     	
     	String batchName= "InValidBatchName";
     	final MvcResult mvcResult = mockMvc.perform(get("/lms/batches/batchName/{batchName}", batchName).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
        .andReturn();
 	   
     	 String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(404, mvcResult.getResponse().getStatus());

         ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
         String message = apiResponse.getMessage();
         
         assertEquals(false, apiResponse.isSuccess());
         assertEquals("programBatch with idInValidBatchNamenot found", message);
   }
     
   
    @Test
    public void testGetBatchByProgram() throws Exception{
 	   
 	   final MvcResult mvcResult = mockMvc.perform(get("/lms/batches/program/{programId}", 1).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
        .andReturn();
 	   
 	  assertEquals(200, mvcResult.getResponse().getStatus());
 	 }
    
    @Test
    public void testGetBatchByInvalidProgram() throws Exception{
     	
    	 final MvcResult mvcResult = mockMvc.perform(get("/lms/batches/program/{programId}", 1234).contextPath("/lms")
                 .header("Authorization", "Bearer " + token)
                 .contentType("application/json"))
         .andReturn();
 	   
     	 String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(404, mvcResult.getResponse().getStatus());

         ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
         String message = apiResponse.getMessage();
         
         assertEquals(false, apiResponse.isSuccess());
         assertEquals("batch with this programId 1234 not found", message);
   }
   
    
    @Test
    @Transactional
    @DirtiesContext
    public void testCreateBatch() throws Exception{
 	   
    	 BatchDTO dummyBatchDTO =new BatchDTO();
    	 dummyBatchDTO.setBatchName("Test DataScience Batch02");
    	 dummyBatchDTO.setBatchDescription("TestDatascience Desc");
    	 dummyBatchDTO.setBatchNoOfClasses(5);
    	 dummyBatchDTO.setBatchStatus("Active");
    	 dummyBatchDTO.setProgramId(2L);
    	 dummyBatchDTO.setProgramName("DataScience");
    	
    	 String requestJson =obj.writeValueAsString(dummyBatchDTO);
    	 
         final MvcResult mvcResult=mockMvc.perform(post("/lms/batches").contextPath("/lms").content(requestJson)
                         .header("Authorization", "Bearer " + token).contentType("application/json"))
                 .andReturn();
         assertEquals(201, mvcResult.getResponse().getStatus());
    }
    
    
    @Test
    @Transactional
    @DirtiesContext
    public void testUpdateBatch() throws Exception{
 	   
 	   
 	   int batchId=progBatchRepository.findAll().get(0).getBatchId();
 	   BatchDTO dummyBatchDTO =new BatchDTO();
 	  
	   dummyBatchDTO.setBatchDescription("SDET Update Batch");
	   dummyBatchDTO.setBatchName("B02");
	   dummyBatchDTO.setBatchNoOfClasses(6);
	   dummyBatchDTO.setBatchStatus("Active");
	   dummyBatchDTO.setProgramId(2L);
	   dummyBatchDTO.setProgramName("SDET Test");
       String requestJson =obj.writeValueAsString(dummyBatchDTO);
       final MvcResult mvcResult=mockMvc.perform(put("/lms/batches/{batchId}",batchId).contextPath("/lms").content(requestJson)
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
        String responseBody = mvcResult.getResponse().getContentAsString();
        BatchDTO batchDTO=obj.readValue(responseBody, BatchDTO.class);
        assertEquals(batchDTO.getBatchDescription(),dummyBatchDTO.getBatchDescription());
 	 }
    
   @Test
   @Transactional
    public void testdeleteBatch() throws Exception{
 	   
    	int batchId=progBatchRepository.findAll().get(0).getBatchId();
    	System.out.println("batchId=="+batchId);
        final MvcResult mvcResult=mockMvc.perform(delete("/lms/batches/{id}",batchId).contextPath("/lms")
                        .header("Authorization", "Bearer " + token).contentType("application/json"))
                .andReturn();
        assertEquals(200,mvcResult.getResponse().getStatus());
 	   
    }
    
    
    
  
  
	

}
