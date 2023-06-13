package com.numpyninja.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.WithMockAdminStaff;
import com.numpyninja.lms.config.WithMockStaff;
import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.entity.Attendance;
import com.numpyninja.lms.repository.AttendanceRepository;
import com.numpyninja.lms.services.AttendanceServices;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
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
@WebMvcTest(AttendanceController.class)
@WithMockUser
class AttendanceControllerTest extends AbstractTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttendanceServices attendanceServices;

    @Autowired
    private ObjectMapper objectMapper;

    private AttendanceServices attendanceService;

    private AttendanceDto attendanceDtos;
    private AttendanceDto mockAttendanceDto;
    private AttendanceRepository attendanceRepository;
    private static Attendance attendance;

    private List<AttendanceDto> attendanceList;

    @BeforeEach
    public void setup() {
        setMockAttendanceAndDto();
    }

    private void setMockAttendanceAndDto() {

        attendanceDtos = new AttendanceDto(7L, 7L, "U03", "Present", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        attendanceList = new ArrayList<AttendanceDto>();
    }


    @DisplayName("test to get all attendance")
    @Test
    @SneakyThrows
    void testGetAllAttendance() {
        AttendanceDto attendanceDtos2 = attendanceDtos;
        attendanceDtos2.setAttId(7L);
        attendanceDtos2.setCsId(7L);
        attendanceDtos2.setStudentId("U03");
        attendanceDtos2.setAttendance("Present");
        attendanceDtos2.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
        attendanceDtos2.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));

        attendanceList.add(attendanceDtos);
        attendanceList.add(attendanceDtos2);

        when(attendanceServices.getAllAttendances()).thenReturn((List<AttendanceDto>) attendanceList);

        ResultActions resultActions = mockMvc.perform(get("/attendance"));

        resultActions.andExpectAll(status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(attendanceList)),
                        MockMvcResultMatchers.jsonPath("$", hasSize(((List<AttendanceDto>) attendanceList).size())),
                        MockMvcResultMatchers.jsonPath("$[0].attendance", equalTo(attendanceDtos.getAttendance())),
                        MockMvcResultMatchers.jsonPath("$[1].attendance", equalTo(attendanceDtos2.getAttendance())))
                .andDo(print());
        verify(attendanceServices).getAllAttendances();


    }

    @DisplayName("test to get attendance by Id")
    @Test
    @SneakyThrows
    void testFindById() {

        Long attId = 6L;
        given(attendanceServices.getAttendanceById(attId)).willReturn(attendanceDtos);
        ResultActions resultActions = mockMvc.perform(get("/attendance/{id}", attId));
        resultActions.andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.attendance", equalTo(attendanceDtos.getAttendance())));


    }

    @DisplayName("test to get attendance by studentId")
    @Test
    @SneakyThrows
    void testGetAttendancesForStudent() {

        String studentId = "U03";
        AttendanceDto mockAttendanceDto2 = attendanceDtos;
        attendanceDtos.setAttId(2L);
        attendanceDtos.setAttendance("Present");
        ArrayList<AttendanceDto> attendancedtoList = new ArrayList();
        attendancedtoList.add(mockAttendanceDto);
        attendancedtoList.add(mockAttendanceDto2);
        given(attendanceServices.getAttendanceForStudent(studentId))
                .willReturn(attendancedtoList);

        //when
        ResultActions response = mockMvc.perform(get("/attendance/student/{studentId}", studentId));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(attendancedtoList.size())));

    }

    @DisplayName("test to get attendance by classId")
    @Test
    @SneakyThrows
    void testGetAttendancesbyClass() {


        long classId = 7;
        AttendanceDto mockAttendanceDto2 = attendanceDtos;
        attendanceDtos.setAttId(2L);
        attendanceDtos.setAttendance("Present");
        ArrayList<AttendanceDto> attendancedtoList = new ArrayList();
        attendancedtoList.add(mockAttendanceDto);
        attendancedtoList.add(mockAttendanceDto2);
        given(attendanceServices.getAttendanceByClass(classId))
                .willReturn(attendancedtoList);

        //when
        ResultActions response = mockMvc.perform(get("/attendance/class/{classId}", classId));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(attendancedtoList.size())));

    }

    @DisplayName("test to get attendance by batchId")
    @Test
    @SneakyThrows
    void testGetAttendancesbyBatch() {
        //given
        Integer batchId = 7;
        AttendanceDto mockAttendanceDto2 = attendanceDtos;
        attendanceDtos.setAttId(2L);
        attendanceDtos.setAttendance("Present");
        ArrayList<AttendanceDto> attendancedtoList = new ArrayList();
        attendancedtoList.add(mockAttendanceDto);
        attendancedtoList.add(mockAttendanceDto2);
        given(attendanceServices.getAttendanceByBatch(batchId))
                .willReturn(attendancedtoList);

        //when
        ResultActions response = mockMvc.perform(get("/attendance/batch/{batchId}", batchId));

        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(attendancedtoList.size())));
    }


    @DisplayName("test to delete attendance")
    @Test
    @WithMockAdminStaff
    @SneakyThrows
    void testDeleteAttendance() throws Exception {
        //given
        Long attId = 8L;
        BDDMockito.willDoNothing().given(attendanceServices).deleteAttendance(attId);

        //when
        ResultActions response = mockMvc.perform(delete("/attendance/{id}", attId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        response.andExpect(status().isOk())
                .andDo(print());
    }


    @DisplayName("test to create  attendance")
    @WithMockAdminStaff
    @Test
    @SneakyThrows
    void testCreateAttendance() {
        given(attendanceServices.createAttendance(ArgumentMatchers.any(AttendanceDto.class)))
                .willAnswer((i) -> i.getArgument(0));
        ResultActions resultActions = mockMvc.perform(post("/attendance")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(attendanceDtos)));

        resultActions.andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.attId", equalTo(attendanceDtos.getAttId()), Long.class))
                .andExpect((ResultMatcher) jsonPath("$.attendance", equalTo(attendanceDtos.getAttendance())));


    }


    @DisplayName("test to update attendance")
    @WithMockAdminStaff
    @Test
    @SneakyThrows
    void testUpdateAttendance() throws Exception {
        Long attId = 1L;
        AttendanceDto updateAttendanceDTO = attendanceDtos;
        updateAttendanceDTO.setAttendance("Present");
        given(attendanceServices.updateAttendance(any(AttendanceDto.class), any(Long.class)))
                .willReturn(updateAttendanceDTO);
        ResultActions resultActions = mockMvc.perform(put("/attendance/{id}", attId)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateAttendanceDTO)));
        resultActions.andExpect(status().isOk()).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.attId", equalTo(updateAttendanceDTO.getAttId()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attendance", equalTo(updateAttendanceDTO.getAttendance())));
    }

}
