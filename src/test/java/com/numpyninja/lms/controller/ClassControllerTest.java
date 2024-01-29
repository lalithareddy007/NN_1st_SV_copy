package com.numpyninja.lms.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.WithMockAdmin;
import com.numpyninja.lms.config.WithMockStaff;
import com.numpyninja.lms.config.WithMockStudent;
import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.ClassRecordingDTO;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.services.ClassService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ClassController.class)
@WithMockUser
class ClassControllerTest extends AbstractTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassService classService;
    @Autowired
    ObjectMapper objectMapper;
    private static List<ClassDto> classDtoList;

    private static List<ClassRecordingDTO> classRecordingDTOList;

    public Date setdate(){
        String sDate = "11/02/2022";
        Date classDate = null;
        try {
            classDate = new SimpleDateFormat("dd/MM/yyyy").parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return classDate;
    }
    @BeforeAll
    public static void setData() {
        String sDate = "11/02/2022";
        Date classDate = null;
        try {
            classDate = new SimpleDateFormat("dd/MM/yyyy").parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ClassDto classDto1 = new ClassDto(1L, 1, 1, classDate, "Selenium1",
                "Active","UO2", "Selenium1 Class", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath");
        ClassDto classDto2 = new ClassDto(1L, 1, 1, classDate, "Selenium2","Active",
                "UO3", "Selenium2 Class", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath");
        ClassDto classDto3 = new ClassDto(1L, 1, 1, classDate, "Selenium3","Active",
                "UO2", "Selenium3 Class", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath");
        classDtoList = Arrays.asList(classDto1,classDto2,classDto3);

    }
    @Nested
    class GetOperation {
        @DisplayName("test - to Get All Classes")
        @SneakyThrows
        @Test
        @WithMockStudent
        void testGetAllClass() {
            //given
            given(classService.getAllClasses()).willReturn(classDtoList);

            //when
            ResultActions response = mockMvc.perform(get("/allClasses"));

            //then
            response.andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(classDtoList.size())));

            verify(classService).getAllClasses();
        }

        @DisplayName("test - Get all class By ClassTopic ")
        @SneakyThrows
        @Test
        @WithMockAdmin
        void testGetAllClassesByClassTopic() {
            //given
            ClassDto classDto1 = classDtoList.get(0);
            given(classService.getClassesByClassTopic(classDto1.getClassTopic())).willReturn(classDtoList);

            //when
            ResultActions response = mockMvc.perform(get("/classes/{classTopic}", classDto1.getClassTopic()));

            //then
            response.andExpectAll(status().isOk(),
                            jsonPath("$", hasSize(3)))
                    .andDo(print());

        }

        @DisplayName("test - Get all class By ClassTopic not found")
        @SneakyThrows
        @Test
        @WithMockStaff
        void testGetAllClassesByClassTopicNotFound() {

            String classTopic = "xyz";
            String message = "Class with class Topic  not found";
            when(classService.getClassesByClassTopic(ArgumentMatchers.any(String.class)))
                    .thenThrow(new ResourceNotFoundException(message));

            //when
            ResultActions response = mockMvc.perform(get("/classes/{classTopic}", classTopic)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(classDtoList)));
            //then
            response.andExpectAll(status().isNotFound(),
                            jsonPath("$.message").value(message))
                    .andDo(print());

            verify(classService).getClassesByClassTopic(ArgumentMatchers.any(String.class));


        }

        @DisplayName("test - Get Classes By ClassId")
        @SneakyThrows
        @Test
        void testGetClassById() {
            //given
            ClassDto classDto3 = classDtoList.get(2);
            given(classService.getClassByClassId(classDto3.getCsId()))
                    .willReturn(classDto3);

            //when
            ResultActions response = mockMvc.perform(get("/class/{classId}", classDto3.getCsId()));

            //then
            response.andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.classTopic", is(classDto3.getClassTopic())));

            verify(classService).getClassByClassId(classDto3.getCsId());

        }

        @DisplayName("test -Class By ClassId not found")
        @SneakyThrows
        @Test
        @WithMockUser
        void testGetClassByIdNotFound() {
            //given
            Long classId = 3L;
            String message = "Class with Class ID  not found";
            when(classService.getClassByClassId(ArgumentMatchers.any(Long.class)))
                    .thenThrow(new ResourceNotFoundException(message));

            //when
            ResultActions response = mockMvc.perform(get("/class/{classId}", classId));

            //then
            response.andExpectAll(status().isNotFound(),
                            jsonPath("$.message").value(message))
                    .andDo(print());

            verify(classService).getClassByClassId(ArgumentMatchers.any(Long.class));

        }


        @DisplayName("test - Get all Class  by batchId ")
        @SneakyThrows
        @Test
        @WithMockUser
        void testGetClassByBatchId() {
            //given
            Integer batchId = 1;
            given(classService.getClassesByBatchId(batchId)).willReturn(classDtoList);

            //when
            ResultActions response = mockMvc.perform(get("/classesbyBatch/{batchId}", batchId));

            //then
            response.andExpectAll(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(classDtoList.size())));
            verify(classService).getClassesByBatchId(batchId);

        }
        @DisplayName("test -Classes By BatchId not found")
        @SneakyThrows
        @Test
        @WithMockUser
        void testGetClassByBatchIdNotFound() {
            //given
            Integer batchId = 3;
            String message = "Class with Batch ID 3 not found";
            when(classService.getClassesByBatchId(ArgumentMatchers.any(Integer.class)))
                    .thenThrow(new ResourceNotFoundException(message));
            //when
            ResultActions response = mockMvc.perform(get("/classesbyBatch/{batchId}", batchId));

            //then
            response.andExpectAll(status().isNotFound(),
                            jsonPath("$.message").value(message))
                    .andDo(print());


        }

        @DisplayName("test - Get all Class by staffId ")
        @SneakyThrows
        @Test
        @WithMockStudent
        void testGetClassByStaffId() {
            //gives
            String staffId = "U02";
            given(classService.getClassesByStaffId(staffId)).willReturn(classDtoList);
            //when
            ResultActions response = mockMvc.perform(get("/classesByStaff/{staffId}", staffId));

            //then
            response.andExpectAll(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(classDtoList.size())));
            verify(classService).getClassesByStaffId(staffId);

        }

        @DisplayName("test -Classes By StaffId not found")
        @SneakyThrows
        @Test
        @WithMockUser
        void testGetClassByStaffIdNotFound() {
            //given
            String staffId = "U05";
            String message = "Class with Staff ID  not found";
            when(classService.getClassesByStaffId(ArgumentMatchers.any(String.class)))
                    .thenThrow(new ResourceNotFoundException(message));
            //when
            ResultActions response = mockMvc.perform(get("/classesByStaff/{staffId}", staffId));

            //then
            response.andExpectAll(status().isNotFound(),
                            jsonPath("$.message").value(message))
                    .andDo(print());
        }

        @DisplayName("test - to Get All Class Recordings")
        @SneakyThrows
        @Test
        @WithMockStudent
        void testGetAllClassRecordings() throws Exception {
            //given
            ClassRecordingDTO recording1 = new ClassRecordingDTO(1L, "c:/RecordingPath");
            ClassRecordingDTO recording2 = new ClassRecordingDTO(2L, "c:/RecordingPath");
            List<ClassRecordingDTO> recordings = Arrays.asList(recording1, recording2);
            given(classService.getAllClassRecordings()).willReturn( recordings);

            //when
            ResultActions response = mockMvc.perform(get("/classrecordings"));

            //then
            response.andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(recordings.size())));

            verify(classService, times(1)).getAllClassRecordings();
        }
    }


    @Nested
    class CreateOperation {

        @DisplayName("test - to create a new Class By admin")
        @SneakyThrows
        @Test
        @WithMockAdmin
        void testCreateClass() {
            ClassDto classDto1 = classDtoList.get(0);
            classDto1.setClassTopic("Selenium-01");
            //given
            given(classService.createClass(ArgumentMatchers.any(ClassDto.class)))
                    .willReturn(classDto1);

            //when
            ResultActions response = mockMvc.perform(post("/CreateClassSchedule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(classDto1)));

            //then
            response.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.csId", is(classDto1.getCsId()), Long.class))
                    .andExpect(jsonPath("$.classTopic", is(classDto1.getClassTopic())));

        }
        @DisplayName("test - to create a new Class by staff")
        @SneakyThrows
        @WithMockStaff
        @Test
        void testCreateClassByStaff() {
            ClassDto classDto2 = classDtoList.get(1);
            classDto2.setClassTopic("Selenium-02");
            given(classService.createClass(ArgumentMatchers.any(ClassDto.class))).willReturn(classDto2);

            ResultActions response = mockMvc.perform(post("/CreateClassSchedule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(classDto2)));

            response.andExpect(status().isForbidden());
        }
    }

    @Nested
    class PutOperation {
        @DisplayName("test - to Update Class Schedule by Id")
        @SneakyThrows
        @Test
        @WithMockAdmin
        void testUpdateClassByClassId() {
            Date date = setdate();
            ClassDto updateClassDTO = new ClassDto(1L, 1, 1, date, "Selenium-01","Active",
                    "UO2", "Selenium1 Introduction Class", "OK",
                    "c:/ClassNotes",
                    "c:/RecordingPath");
            Long classId = 1L;

            given(classService.updateClassByClassId(ArgumentMatchers.any(Long.class),
                    ArgumentMatchers.any(ClassDto.class))).willReturn(updateClassDTO);

            ResultActions response = mockMvc.perform(put("/updateClass/{classId}", classId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateClassDTO)));

            response.andExpectAll(status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON),
                            jsonPath("$.classTopic", is(updateClassDTO.getClassTopic())))
                    .andDo(print());
        }

        @DisplayName("Test - Update Class Schedule by Class ID (Forbidden)")
        @Test
        @SneakyThrows
        @WithMockStudent
        void testUpdateClassScheduleByIdForbidden() {
            Date date = setdate();
            ClassDto updateClassDTO = new ClassDto(1L, 1, 1, date, "Selenium-01","Active",
                    "UO2", "Selenium1 Introduction Class", "OK",
                    "c:/ClassNotes",
                    "c:/RecordingPath");
            Long classId = 1L;

            given(classService.updateClassByClassId(ArgumentMatchers.any(Long.class),
                    ArgumentMatchers.any(ClassDto.class))).willReturn(updateClassDTO);

            ResultActions response = mockMvc.perform(put("/updateClass/{classId}", classId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateClassDTO)));

            response.andExpectAll(status().isForbidden());
        }

        @DisplayName("test - Update class by Id  Not Found")
        @SneakyThrows
        @Test
        @WithMockAdmin
        void testUpdateClassByIdNotFound() {
            Long classId = 7L;
            String message = "Class with class id  not found";
            Date date = setdate();
            ClassDto updateClassDTO = new ClassDto(1L, 1, 1, date, "Selenium-01","Active",
                    "UO2", "Selenium1 Introduction Class", "OK",
                    "c:/ClassNotes",
                    "c:/RecordingPath");
            when(classService.updateClassByClassId(ArgumentMatchers.any(Long.class),
                    ArgumentMatchers.any(ClassDto.class))).thenThrow(new ResourceNotFoundException(message));

            ResultActions response = mockMvc.perform(put("/updateClass/{classId}", classId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateClassDTO)));

            response.andExpectAll(status().isNotFound(),
                            jsonPath("$.message").value(message))
                    .andDo(print());
        }


    }

    @Nested
    class DeleteOperation {

        @DisplayName("test - to delete Class By Admin")
        @SneakyThrows
        @Test
        @WithMockAdmin
        void testDeleteClass() {

            //given
            Long classId = 2L;
            given(classService.deleteByClassId(classId)).willReturn(true);
            //when
            ResultActions response = mockMvc.perform(delete("/deleteByClass/{classId}", classId));
            //then
            response.andExpect(status().isOk());

        }
        @DisplayName("test - to delete Class By User")
        @SneakyThrows
        @Test
        @WithMockStudent
        void testDeleteClassByStaff() {

            //given
            Long classId = 2L;
            given(classService.deleteByClassId(classId)).willReturn(true);
            //when
            ResultActions response = mockMvc.perform(delete("/deleteByClass/{classId}", classId));
            //then
            response.andExpect(status().isForbidden());

        }

        @DisplayName("test - Delete class ById Not Found")
        @SneakyThrows
        @Test
        @WithMockAdmin
        void testDeleteClassByIdNotFound() {
            //given
            Long classId = 6L;
            given(classService.deleteByClassId(classId)).willReturn(false);

            //when
            ResultActions response = mockMvc.perform(delete("/deleteByClass/{classId}", classId));

            //then
            response.andExpect(status().isNotFound());

        }

    }

}