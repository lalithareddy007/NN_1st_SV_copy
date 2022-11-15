package com.numpyninja.lms.controller;



import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.exception.ResourceNotFoundException;

import com.numpyninja.lms.services.ClassService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.BDDMockito.*;


import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.fasterxml.jackson.databind.ObjectMapper;

    @ExtendWith(MockitoExtension.class)
    @WebMvcTest(ClassController.class )
    class ClassControllerTest {

        @Autowired
        private MockMvc mockMvc;


        @MockBean
        private ClassService classService;



        @Autowired
        private ObjectMapper objectMapper;

        private ClassDto mockClassDto;

        private List<ClassDto> classDtoList;

        @BeforeEach
        public void setup() {

            setMockClassAndDto();

        }

    private void setMockClassAndDto() {
        String sDate = "11/02/2022";
        Date classDate = null;
        try {
            classDate = new SimpleDateFormat("dd/mm/yyyy").parse(sDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       mockClassDto = new ClassDto (1L,1,1,classDate ,"Selenium1",
                "UO2","Selenium Class", "OK",
                "c:/ClassNotes",
                "c:/RecordingPath",Timestamp.valueOf(LocalDateTime.now()),Timestamp.valueOf(LocalDateTime.now()));
        classDtoList = new ArrayList<ClassDto>();
    }






        @Nested
        class GetOperation {
            @DisplayName("test - to Get All Classes")
            @SneakyThrows
            @Test
            void testGetAllClass() {
                //given
                ClassDto mockClassDto2 = mockClassDto;
                mockClassDto2.setCsId(2L);
                mockClassDto2.setClassTopic("Selenium Test");
                classDtoList.add(mockClassDto);
                classDtoList.add(mockClassDto2);

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
            public void testGetAllClassesByClassTopic() {
                //given
                classDtoList.add(mockClassDto);
                String classTopic = "Selenium1";
                given(classService.getClassesByClassTopic(classTopic)).willReturn(classDtoList);

                //when
                ResultActions response = mockMvc.perform(get("/classes/{classTopic}", classTopic));

                //then
                response.andExpectAll(status().isOk(),
                                jsonPath("$", hasSize(1)))
                        .andDo(print());

                verify(classService).getClassesByClassTopic(classTopic);
            }

            @DisplayName("test - Get all class By ClassTopic not found")
            @SneakyThrows
            @Test
            public void testGetAllClassesByClassTopicNotFound() {

                String classTopic = "xyz";
                String message = "Class with class Topic  not found";
                classDtoList.add(mockClassDto);
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
                Long classId = 1L;
                given(classService.getClassByClassId(classId))
                        .willReturn(mockClassDto);

                //when
                ResultActions response = mockMvc.perform(get("/class/{classId}", classId));

                //then
                response.andExpect(status().isOk())
                        .andDo(print())
                        .andExpect(jsonPath("$.classTopic", is(mockClassDto.getClassTopic())));

                verify(classService).getClassByClassId(classId);

            }

            @DisplayName("test -Class By ClassId not found")
            @SneakyThrows
            @Test
            void testGetClassByIdNotFound() {
                //given
                Long classId = 3L;
                String message = "Class with Class ID  not found";
                classDtoList.add(mockClassDto);
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
            void testGetClassByBatchId() {
                //given
                Integer batchId = 1;
                ClassDto mockClassDto2 = mockClassDto;
                mockClassDto2.setCsId(4L);
                mockClassDto2.setClassTopic("PostgreSql Test");
                classDtoList.add(mockClassDto);
                classDtoList.add(mockClassDto2);

                given(classService.getClassesByBatchId(batchId))
                        .willReturn(classDtoList);

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
            void testGetClassByBatchIdNotFound() {
                //given
                Integer batchId = 3;
                String message = "Class with Batch ID 3 not found";
                classDtoList.add(mockClassDto);
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
            void testGetClassByStaffId() {
                //gives
                String staffId = "U02";
                ClassDto mockClassDto2 = mockClassDto;
                mockClassDto2.setCsId(3L);
                mockClassDto2.setClassTopic("Java Test");
                classDtoList.add(mockClassDto);
                classDtoList.add(mockClassDto2);

                given(classService.getClassesByStaffId(staffId))
                        .willReturn(classDtoList);

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
            void testGetClassByStaffIdNotFound() {
                //given
                String staffId = "U05";
                String message = "Class with Staff ID  not found";
                classDtoList.add(mockClassDto);
                when(classService.getClassesByStaffId(ArgumentMatchers.any(String.class)))
                        .thenThrow(new ResourceNotFoundException(message));
                //when
                ResultActions response = mockMvc.perform(get("/classesByStaff/{staffId}", staffId));

                //then
                response.andExpectAll(status().isNotFound(),
                                jsonPath("$.message").value(message))
                        .andDo(print());
            }
        }
        @DisplayName("test - to create a new Class")
        @SneakyThrows
        @Test
        void testCreateClass() {
            //given
            given(classService.createClass(ArgumentMatchers.any(ClassDto.class)))
                    .willAnswer((i) -> i.getArgument(0));

            //when
            ResultActions response = mockMvc.perform(post("/CreateClassSchedule")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockClassDto)));

            //then
            response.andExpect(status().isCreated())
                    .andExpect(jsonPath("$.csId", is(mockClassDto.getCsId()), Long.class))
                    .andExpect(jsonPath("$.classTopic", is(mockClassDto.getClassTopic())));

            verify(classService).createClass(ArgumentMatchers.any(ClassDto.class));
        }
        @Nested
        class PutOperation {
            @DisplayName("test - to Update Class Schedule by Id")
            @SneakyThrows
            @Test
            void testUpdateClassByClassId() {
                //given
                Long classId = 1L;
                ClassDto updatedClassDto = mockClassDto;
                updatedClassDto.setClassTopic("New Selenium Class");
                when(classService.updateClassByClassId(ArgumentMatchers.any(Long.class)
                        , ArgumentMatchers.any(ClassDto.class))).thenReturn(updatedClassDto);


                //when
                ResultActions response = mockMvc.perform(put("/updateClass/{classId}", classId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClassDto)));
                System.out.println(response);

                //then
                response.andExpectAll(status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON),
                                jsonPath("$.classTopic", is(updatedClassDto.getClassTopic())))
                        .andDo(print());

                verify(classService).updateClassByClassId(ArgumentMatchers.any(Long.class),
                        ArgumentMatchers.any(ClassDto.class));


            }

            @DisplayName("test - Update class by Id  Not Found")
            @SneakyThrows
            @Test
            public void testUpdateClassByIdNotFound() {

                //given
                Long classId = 7L;
                String message = "Class with class id  not found";
                ClassDto updatedClassDto = mockClassDto;
                updatedClassDto.setClassTopic("New Selenium Class");
                when(classService.updateClassByClassId(ArgumentMatchers.any(Long.class),
                        ArgumentMatchers.any(ClassDto.class))).thenThrow(new ResourceNotFoundException(message));

                //when
                ResultActions response = mockMvc.perform(put("/updateClass/{classId}", classId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedClassDto)));

                //then
                response.andExpectAll(status().isNotFound(),
                                jsonPath("$.message").value(message))
                        .andDo(print());

                verify(classService).updateClassByClassId(ArgumentMatchers.any(Long.class),
                        ArgumentMatchers.any(ClassDto.class));
            }

        }
        @Nested
        class DeleteOperation {

            @DisplayName("test - to delete Class")
            @SneakyThrows
            @Test
            void testDeleteClass() {

                //given
                Long classId = 2L;
                given(classService.
                        deleteByClassId(classId)).willReturn(true);
                //when
                ResultActions response = mockMvc.perform(delete("/deletebyClass/{classId}", classId));
                //then
                response.andExpect(status().isOk());

                verify(classService).deleteByClassId(classId);

            }


            @DisplayName("test - Delete class ById Not Found")
            @SneakyThrows
            @Test
            public void testDeleteClassByIdNotFound() {
                //given
                Long classId = 6L;
                given(classService.
                        deleteByClassId(classId)).willReturn(false);

                //when
                ResultActions response = mockMvc.perform(delete("/deletebyClass/{classId}", classId));

                //then
                response.andExpect(status().isNotFound());

                verify(classService).deleteByClassId(classId);
            }

        }

    }