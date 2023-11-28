package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
import com.numpyninja.lms.repository.AttendanceRepository;
import com.numpyninja.lms.repository.ClassRepository;
import com.numpyninja.lms.services.AttendanceServices;
import com.numpyninja.lms.services.ClassService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(AttendanceController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class AttendanceControllerIT {
	
	 private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private AttendanceRepository attRepository;
    
    @Autowired
    private AttendanceServices attService;
    
    @Autowired
    private ClassRepository clsRepository;
    

    @Autowired
    private ClassService clsService;

    ObjectMapper obj = new ObjectMapper();

    private String token;
    
    private static Integer batchId;
    
    private static Long classId;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        // fetch a token
        final LoginDto loginDto = new LoginDto("John.Matthew@gmail.com", "John123");
        final String loginBody = obj.writeValueAsString(loginDto);

        final MvcResult mvcResult = mockMvc.perform(post("/lms/login").contextPath("/lms")
                        .contentType("application/json")
                        .content(loginBody))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());

        String loginResponseBody = mvcResult.getResponse().getContentAsString();
        final JwtResponseDto jwtResponseDto = obj.readValue(loginResponseBody, JwtResponseDto.class);
        token = jwtResponseDto.getToken();

        assertNotNull(token, "token is null");
    }
    
    //test create attendance
    @Test
    @Order(1)
    public void testCreateAndSaveAttendance() throws Exception {
    	
        final AttendanceDto attendanceDto = new AttendanceDto();
        attendanceDto.setCsId((long) 1);
        attendanceDto.setStudentId("U09");
        attendanceDto.setAttendance("Present");
        attendanceDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
        attendanceDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        
        String requestJson =obj.writeValueAsString(attendanceDto);
        final MvcResult mvcResult=mockMvc.perform(post("/lms/attendance").contextPath("/lms").content(requestJson)
                .header("Authorization", "Bearer " + token).contentType("application/json"))
        .andReturn();
        assertEquals(201, mvcResult.getResponse().getStatus());

        AttendanceDto responseDto = obj.readValue(requestJson, AttendanceDto.class);
        assertEquals((long)1, responseDto.getCsId());
        assertEquals("U09", responseDto.getStudentId());
        assertEquals("Present", responseDto.getAttendance());

    }
    
    //test get all attendance
    @Test
    @Order(2)
    public void testGetAllAttendance() throws Exception {
        System.out.println(token);
        final MvcResult mvcResult = mockMvc.perform(get("/lms/attendance").contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();

        assertEquals(200, mvcResult.getResponse().getStatus());
    }
    

    //test get attendance by id
    @Test
    @Order(3)
    public void testGetattendanceById() throws Exception {
    	long attendanceId=attRepository.findAll().get(0).getAttId();
    	
        final MvcResult mvcResult = mockMvc.perform(get("/lms/attendance/{attendanceId}" , attendanceId).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json"))
        .andReturn();
         String responseBody =  mvcResult.getResponse().getContentAsString();
         assertEquals(200, mvcResult.getResponse().getStatus());

         AttendanceDto attDTO= obj.readValue(responseBody, AttendanceDto.class);
        assertEquals(attendanceId,attDTO.getAttId());

    }
    
    //test get attendance by student id
     @Test
     @Order(4)
     public void testGetAttendanceByStudentId() throws Exception {
    	 
    	 long attId = attRepository.findAll().get(0).getAttId();
    	 AttendanceDto attDto = attService.getAttendanceById(attId) ;
    	 String studentId = attDto.getStudentId();
    	 
         final MvcResult mvcResult = mockMvc.perform(get("/lms/attendance/student/{studentId}" , studentId).contextPath("/lms")
                         .header("Authorization", "Bearer " + token)
                         .contentType("application/json"))
                 .andReturn();

         String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(200, mvcResult.getResponse().getStatus());

         List<AttendanceDto> responseDtoList = obj.readValue(jsonResponse, new TypeReference<List<AttendanceDto>>() {
         });
         AttendanceDto attendanceDtoforStudentId = null;
         for( AttendanceDto attendanceDto: responseDtoList){
             if(attId == attendanceDto.getAttId()) {
            	 attendanceDtoforStudentId = attendanceDto;
                 break;
             }
         }
         assertNotNull(attendanceDtoforStudentId, "attendanceDto for StudentId is null");
         assertEquals(attDto.getAttId(), attendanceDtoforStudentId.getAttId());
         assertEquals(attDto.getCsId(), attendanceDtoforStudentId.getCsId());
         assertEquals(attDto.getStudentId(), attendanceDtoforStudentId.getStudentId());
         assertEquals(attDto.getAttendance(), attendanceDtoforStudentId.getAttendance());
     }
     
     //test get attendance by class id
     @Test
     @Order(5)
     public void testGetAttendanceByClassId() throws Exception {
    	 
    	 long attId = attRepository.findAll().get(0).getAttId();
    	 AttendanceDto attDto = attService.getAttendanceById(attId) ;
    	 Long clsId = attDto.getCsId();
    	 
         final MvcResult mvcResult = mockMvc.perform(get("/lms/attendance/class/" + clsId).contextPath("/lms")
                         .header("Authorization", "Bearer " + token)
                         .contentType("application/json"))
                 .andReturn();

         String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(200, mvcResult.getResponse().getStatus());

         List<AttendanceDto> responseDtoList = obj.readValue(jsonResponse, new TypeReference<List<AttendanceDto>>() {
         });
         AttendanceDto attendanceDtoforClassId = null;
         for( AttendanceDto attendanceDto: responseDtoList){
             if(attId == attendanceDto.getAttId()) {
            	 attendanceDtoforClassId = attendanceDto;
                 break;
             }
         }
         
         assertNotNull(attendanceDtoforClassId, "Attendance For ClassId is null");
         assertEquals(attDto.getAttId(), attendanceDtoforClassId.getAttId());
         assertEquals(attDto.getCsId(), attendanceDtoforClassId.getCsId());
         assertEquals(attDto.getStudentId(), attendanceDtoforClassId.getStudentId());
         assertEquals(attDto.getAttendance(), attendanceDtoforClassId.getAttendance());
     }
 
     //test get attendance by batch id
     @Test
     @Order(6)
     public void testGetAttendanceBybatchId() throws Exception {
    	 
		 ClassDto clsDto = new ClassDto();
		 classId = clsRepository.findAll().get(0).getCsId();
		 
		 clsDto = clsService.getClassByClassId(classId);
    	 
		 batchId = clsDto.getBatchId();
    	 
         final MvcResult mvcResult = mockMvc.perform(get("/lms/attendance/batch/" + batchId).contextPath("/lms")
                         .header("Authorization", "Bearer " + token)
                         .contentType("application/json"))
                 .andReturn();

         String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(200, mvcResult.getResponse().getStatus());

         List<AttendanceDto> responseDtoList = obj.readValue(jsonResponse, new TypeReference<List<AttendanceDto>>() {
         });
         
         AttendanceDto attendanceDtoforBatchId = null;
         
         for( AttendanceDto attendanceDto: responseDtoList){
             if(classId.equals(attendanceDto.getCsId())) {
            	 attendanceDtoforBatchId = attendanceDto;
                 break;
             }
         }
         
         assertNotNull(attendanceDtoforBatchId, "attendanceDtoforBatchId is null");
         assertEquals(clsDto.getCsId(), attendanceDtoforBatchId.getCsId());
       
     }

   //test update attendance by attendance Id
     @Test
     @Order(7)
     public void testUpdateAttendance() throws Exception {
    	 
    	 long attendanceId=attRepository.findAll().get(0).getAttId();

         final AttendanceDto attendanceDto = new AttendanceDto();
        
         attendanceDto.setCsId((long) 5);
         attendanceDto.setStudentId("U04");
         attendanceDto.setAttendance("Absent");

         String requestJson =obj.writeValueAsString(attendanceDto);
         final MvcResult mvcResult=mockMvc.perform(put("/lms/attendance/{attendanceId}",attendanceId).contextPath("/lms").content(requestJson)
                         .header("Authorization", "Bearer " + token).contentType("application/json"))
                 .andReturn();
         
         String responseBody = mvcResult.getResponse().getContentAsString();
         assertEquals(200, mvcResult.getResponse().getStatus());
         
         AttendanceDto attDto=obj.readValue(responseBody, AttendanceDto.class);
         assertEquals(attendanceDto.getAttendance(),attDto.getAttendance());

     }
   
     //test attendance already exist
     @Test
     @Order(8)
     public void testAttendanceAlreadyExist() throws Exception {

         final AttendanceDto attendanceDto = new AttendanceDto();
        
         attendanceDto.setCsId((long) 5);
         attendanceDto.setStudentId("U04");
         attendanceDto.setAttendance("Absent");
         attendanceDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
         attendanceDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));

         String requestJson =obj.writeValueAsString(attendanceDto);
         final MvcResult mvcResult=mockMvc.perform(post("/lms/attendance").contextPath("/lms").content(requestJson)
                         .header("Authorization", "Bearer " + token).contentType("application/json"))
                 .andReturn();
         
         String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(400, mvcResult.getResponse().getStatus());
         
         ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
         String message = apiResponse.getMessage();
         
         assertEquals(false, apiResponse.isSuccess());
         assertEquals("Attendance record already exists for class 5 and student U04", message);
         
     }
     
     
     //test create attendance with invalid student Id
     @Test
     @Order(9)
     public void testCreateAttendanceWithInvalidStudentId() throws Exception {

         final AttendanceDto attendanceDto = new AttendanceDto();
        
         attendanceDto.setCsId((long) 5);
         attendanceDto.setStudentId("U56");
         attendanceDto.setAttendance("Absent");
         attendanceDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
         attendanceDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));

         String requestJson =obj.writeValueAsString(attendanceDto);
         final MvcResult mvcResult=mockMvc.perform(post("/lms/attendance").contextPath("/lms").content(requestJson)
                         .header("Authorization", "Bearer " + token).contentType("application/json"))
                 .andReturn();
         
         String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(404, mvcResult.getResponse().getStatus());
         
         ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
         String message = apiResponse.getMessage();
         
         assertEquals(false, apiResponse.isSuccess());
         assertEquals("Student Id U56 not found", message);

     }
     
     //test create attendance with invalid class Id
     @Test
     @Order(10)
     public void testCreateAttendanceWithInvalidClassId() throws Exception {
    	 
    	 final AttendanceDto attendanceDto = new AttendanceDto();
         
         attendanceDto.setCsId((long) 122);
         attendanceDto.setStudentId("U05");
         attendanceDto.setAttendance("Absent");
         attendanceDto.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
         attendanceDto.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));

         String requestJson =obj.writeValueAsString(attendanceDto);
         final MvcResult mvcResult=mockMvc.perform(post("/lms/attendance").contextPath("/lms").content(requestJson)
                         .header("Authorization", "Bearer " + token).contentType("application/json"))
                 .andReturn();
         
         String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(404, mvcResult.getResponse().getStatus());
         
         ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
         String message = apiResponse.getMessage();
         
         assertEquals(false, apiResponse.isSuccess());
         assertEquals("Class not found with Id : 122 ", message);

     }
     
   //test create attendance with invalid attendance Id    
     @Test
     @Order(11)
     public void testGetAttendaceByInvalidId() throws Exception {
     	long attendanceId = 1000;
         final MvcResult mvcResult = mockMvc.perform(get("/lms/attendance/{attendanceId}", attendanceId).contextPath("/lms")
                 .header("Authorization", "Bearer " + token)
                 .contentType("application/json"))
         .andReturn();
         
         String jsonResponse = mvcResult.getResponse().getContentAsString();
         assertEquals(404, mvcResult.getResponse().getStatus());

         ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
         String message = apiResponse.getMessage();

         assertEquals(false, apiResponse.isSuccess());
         assertEquals("Attendance not found with Id : 1000 ", message);
     	
     }    
     
    //test delete attendance
    @Test
    @Order(Integer.MAX_VALUE)
    public void testDeleteAttendance() throws Exception {
    	
    	 long attendanceId=attRepository.findAll().get(0).getAttId();
    	 
         final MvcResult mvcResult=mockMvc.perform(delete("/lms/attendance/{attendanceId}",attendanceId).contextPath("/lms")
                         .header("Authorization", "Bearer " + token).contentType("application/json"))
                 .andReturn();
         assertEquals(200,mvcResult.getResponse().getStatus());
    }
}
