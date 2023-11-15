package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.ApiResponse;
import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.ClassRecordingDTO;
import com.numpyninja.lms.dto.JwtResponseDto;
import com.numpyninja.lms.dto.LoginDto;
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
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureDataJpa
@WebMvcTest(ClassController.class)
@ComponentScan(basePackages = "com.numpyninja.lms.*")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
public class ClassControllerIT {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    ObjectMapper obj = new ObjectMapper();

    private String token;

    private static Long csId;

    private static String classStaffId;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

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

    @Test
    @Order(1)
    public void testCreateAndSaveClass() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(2);
        classDto.setClassTopic("DA");
        classDto.setClassStaffId("U03");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(2);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/CreateClassSchedule").contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(jsonRequest)).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(201, mvcResult.getResponse().getStatus());

        ClassDto responseDto = obj.readValue(jsonResponse, ClassDto.class);
        assertEquals(2, responseDto.getBatchId());
        assertEquals("DA", responseDto.getClassTopic());
        assertEquals("U03", responseDto.getClassStaffId());
        assertEquals("C:\\ClassNotes", responseDto.getClassNotes());
        assertEquals("C:\\Recordings", responseDto.getClassRecordingPath());
        assertEquals(2, responseDto.getClassNo());
        assertEquals("New topic start", responseDto.getClassComments());
        assertEquals("DA class", responseDto.getClassDescription());

        csId = responseDto.getCsId();
        classStaffId = responseDto.getClassStaffId();
    }

    @Test
    @Order(2)
    public void testDuplicateClassTopic() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(2);
        classDto.setClassTopic("DA");
        classDto.setClassStaffId("U03");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(2);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/CreateClassSchedule").contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(jsonRequest)).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(400, mvcResult.getResponse().getStatus());

       ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Class already exists with given  Class Topic : DA ", message);
    }

    @Test
    @Order(3)
    public void testInvalidBatchId() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(10);
        classDto.setClassTopic("DA2");
        classDto.setClassStaffId("U03");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(2);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/CreateClassSchedule").contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(jsonRequest)).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Batch not found with Id : 10 ", message);
    }

    @Test
    @Order(4)
    public void testInvalidStaffId() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(2);
        classDto.setClassTopic("DA3");
        classDto.setClassStaffId("U04");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(2);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(post("/lms/CreateClassSchedule").contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(jsonRequest)).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User not found with Role(Admin/Staff) : U04 ", message);
    }

    @Test
    @Order(5)
    public void testGetAllClassesList() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/allClasses").contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    @Order(6)
    public void testGetClassesById() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/class/" + csId).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        ClassDto responseDto = obj.readValue(jsonResponse, ClassDto.class);
        assertEquals(2, responseDto.getBatchId());
        assertEquals("DA", responseDto.getClassTopic());
        assertEquals("U03", responseDto.getClassStaffId());
        assertEquals("C:\\ClassNotes", responseDto.getClassNotes());
        assertEquals("C:\\Recordings", responseDto.getClassRecordingPath());
        assertEquals(2, responseDto.getClassNo());
        assertEquals("New topic start", responseDto.getClassComments());
        assertEquals("DA class", responseDto.getClassDescription());
    }

    @Test
    @Order(7)
    public void testGetClassByInvalidId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/class/" + 40).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("ClassSchedule is not found for classId :40", message);
    }

    @Test
    @Order(8)
    public void testGetAllClassesByClassTopic() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/classes/" + "DA").contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<ClassDto> classDtosList = obj.readValue(jsonResponse, new TypeReference<List<ClassDto>>() {
        });
        ClassDto classDtoForCsId = null;
        for(ClassDto classDto: classDtosList){
            if(csId.equals(classDto.getCsId())){
                classDtoForCsId = classDto;
            }
        }
        assertNotNull(classDtoForCsId, "classDtoForCsId is null");
        assertEquals(2,  classDtoForCsId.getBatchId());
        assertEquals(2,  classDtoForCsId.getClassNo());
        assertEquals("DA",  classDtoForCsId.getClassTopic());
        assertEquals("U03",  classDtoForCsId.getClassStaffId());
        assertEquals("DA class", classDtoForCsId.getClassDescription());
        assertEquals("New topic start",  classDtoForCsId.getClassComments());
        assertEquals("C:\\ClassNotes", classDtoForCsId.getClassNotes());
        assertEquals("C:\\Recordings",  classDtoForCsId.getClassRecordingPath());
    }

    @Test
    @Order(9)
    public void testGetClassesByInvalidClassTopic() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/classes/" + "streams").contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("classes with class topic Name: streams not found", message);
    }

    @Test
    @Order(10)
    public void testGetClassesByBatchId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/classesbyBatch/" + 2).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<ClassDto> classDtosList = obj.readValue(jsonResponse, new TypeReference<List<ClassDto>>() {
        });
        ClassDto classDtoForCsId = null;
        for(ClassDto classDto: classDtosList){
            if(csId.equals(classDto.getCsId())){
                classDtoForCsId = classDto;
            }
        }
        assertNotNull(classDtoForCsId, "classDtoForCsId is null");
        assertEquals(2,  classDtoForCsId.getBatchId());
        assertEquals(2,  classDtoForCsId.getClassNo());
        assertEquals("DA",  classDtoForCsId.getClassTopic());
        assertEquals("U03",  classDtoForCsId.getClassStaffId());
        assertEquals("DA class", classDtoForCsId.getClassDescription());
        assertEquals("New topic start",  classDtoForCsId.getClassComments());
        assertEquals("C:\\ClassNotes", classDtoForCsId.getClassNotes());
        assertEquals("C:\\Recordings",  classDtoForCsId.getClassRecordingPath());
    }

    @Test
    @Order(11)
    public void testGetClassesByInvalidBatchId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/classesbyBatch/" + 38).contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("classes with this batchId 38not found", message);
    }

    @Test
    @Order(12)
    public void testGetClassesByStaffId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/classesByStaff/" + classStaffId)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<ClassDto> classDtosList = obj.readValue(jsonResponse, new TypeReference<List<ClassDto>>() {
        });
        ClassDto classDtoForCsId = null;
        for(ClassDto classDto: classDtosList){
            if(csId.equals(classDto.getCsId())){
                classDtoForCsId = classDto;
            }
        }
        assertNotNull(classDtoForCsId, "classDtoForCsId is null");
        assertEquals(2,  classDtoForCsId.getBatchId());
        assertEquals(2,  classDtoForCsId.getClassNo());
        assertEquals("DA",  classDtoForCsId.getClassTopic());
        assertEquals("U03",  classDtoForCsId.getClassStaffId());
        assertEquals("DA class", classDtoForCsId.getClassDescription());
        assertEquals("New topic start",  classDtoForCsId.getClassComments());
        assertEquals("C:\\ClassNotes", classDtoForCsId.getClassNotes());
        assertEquals("C:\\Recordings",  classDtoForCsId.getClassRecordingPath());
    }

    @Test
    @Order(13)
    public void testGetClassByInvalidStaffId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/classesByStaff/" + "U04")
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("classes with this staffId U04 not found", message);
    }

    @Test
    @Order(14)
    public void testUpdateClassScheduleById() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(2);
        classDto.setClassTopic("DA");
        classDto.setClassStaffId("U03");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(3);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/updateClass/" + csId)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .content(jsonRequest)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        ClassDto responseDto = obj.readValue(jsonResponse, ClassDto.class);
        assertEquals(3, responseDto.getClassNo());
    }

    @Test
    @Order(15)
    public void testUpdateClassByInvalidId() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(2);
        classDto.setClassTopic("DA");
        classDto.setClassStaffId("U03");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(3);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/updateClass/" + 20)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .content(jsonRequest)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Class not found with Id : 20 ", message);
    }

    @Test
    @Order(16)
    public void testUpdateClassByInvalidBatchId() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(10);
        classDto.setClassTopic("DA");
        classDto.setClassStaffId("U03");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(3);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/updateClass/" + csId)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .content(jsonRequest)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Batch not found with Id : 10 ", message);
    }

    @Test
    @Order(17)
    public void testUpdateClassByInvalidStaffId() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(2);
        classDto.setClassTopic("DA");
        classDto.setClassStaffId("U01");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(3);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/updateClass/" + csId)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .content(jsonRequest)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("User not found with Role(Staff) : U01 ", message);
    }

    @Test
    @Order(18)
    public void testUpdateClassByInvalidClassAndBatchId() throws Exception {
        final ClassDto classDto = new ClassDto();
        classDto.setBatchId(3);
        classDto.setClassTopic("DA");
        classDto.setClassStaffId("U03");
        classDto.setClassNotes("C:\\ClassNotes");
        classDto.setClassRecordingPath("C:\\Recordings");
        classDto.setClassNo(3);
        classDto.setClassComments("New topic start");
        classDto.setClassDescription("DA class");
        classDto.setClassDate(Timestamp.valueOf("2024-10-17 5:30:00"));

        String jsonRequest = obj.writeValueAsString(classDto);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/updateClass/" + csId)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .content(jsonRequest)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("ClassId with " +csId+ " and batchId with 3 not found", message);
    }


    @Test
    @Order(19)
    public void testUpdateClassRecordingByClassId() throws Exception {
        final ClassRecordingDTO classRecordingDTO = new ClassRecordingDTO();
        classRecordingDTO.setClassRecordingPath("C:\\Recordings");

        String jsonRequest = obj.writeValueAsString(classRecordingDTO);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/updateClassrecording/" + csId)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .content(jsonRequest)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        ClassDto responseDto = obj.readValue(jsonResponse, ClassDto.class);
        assertEquals("C:\\Recordings", responseDto.getClassRecordingPath());
    }

    @Test
    @Order(20)
    public void testUpdateClassRecordingByInvalidClassId() throws Exception {
        final ClassRecordingDTO classRecordingDTO = new ClassRecordingDTO();
        classRecordingDTO.setClassRecordingPath("C:\\Recordings");

        String jsonRequest = obj.writeValueAsString(classRecordingDTO);
        final MvcResult mvcResult = mockMvc.perform(put("/lms/updateClassrecording/" + 20)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .content(jsonRequest)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("no record found with 20", message);
    }

    @Test
    @Order(21)
    public void testGetClassRecordingByClassId() throws Exception {
       final MvcResult mvcResult = mockMvc.perform(get("/lms/classrecordings/" + csId)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        ClassRecordingDTO classRecordingDTO = obj.readValue(jsonResponse, ClassRecordingDTO.class);
        assertEquals("C:\\Recordings", classRecordingDTO.getClassRecordingPath());
    }

    @Test
    @Order(22)
    public void testGetClassRecordingByBatchId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/batchrecordings/" + 2)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();
        
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(200, mvcResult.getResponse().getStatus());

        List<ClassRecordingDTO> classRecordingDTOList = obj.readValue(jsonResponse, new TypeReference<List<ClassRecordingDTO>>() {
        });
        ClassRecordingDTO classRecordingDTOForCsId = null;
        for(ClassRecordingDTO classRecordingDTO: classRecordingDTOList){
            if(csId.equals(classRecordingDTO.getCsId())){
                classRecordingDTOForCsId = classRecordingDTO;
            }
        }
        assertNotNull(classRecordingDTOForCsId, "classRecordingDTOForCsId is null");
        assertEquals("C:\\Recordings", classRecordingDTOForCsId.getClassRecordingPath());
    }

    @Test
    @Order(23)
    public void testGetClassRecordingByInvalidBatchId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(get("/lms/batchrecordings/" + 10)
                .contextPath("/lms")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")).andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        assertEquals(404, mvcResult.getResponse().getStatus());

        ApiResponse apiResponse = obj.readValue(jsonResponse, ApiResponse.class);
        String message = apiResponse.getMessage();

        assertEquals(false, apiResponse.isSuccess());
        assertEquals("Class Recording not found with batchId :10", message);
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void testDeleteByClassId() throws Exception {
        final MvcResult mvcResult = mockMvc.perform(delete("/lms/deletebyClass/" + csId)
                        .contextPath("/lms")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json"))
                .andReturn();
        assertEquals(200, mvcResult.getResponse().getStatus());
    }
}
