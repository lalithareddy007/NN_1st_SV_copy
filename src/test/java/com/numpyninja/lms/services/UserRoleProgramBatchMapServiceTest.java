package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.UserRoleProgramBatchMapDto;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserRoleProgramBatchMapMapper;
import com.numpyninja.lms.repository.UserRoleProgramBatchMapRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserRoleProgramBatchMapServiceTest {

    @InjectMocks
    private UserRoleProgramBatchMapService userRoleProgramBatchMapService;

    @Mock
    private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;

    @Mock
    private UserRoleProgramBatchMapMapper userRoleProgramBatchMapMapper;

    private UserRoleProgramBatchMap mockUserRoleProgramBatchMap, mockUserRoleProgramBatchMap2;

    private UserRoleProgramBatchMapDto mockUserRoleProgramBatchMapDto,mockUserRoleProgramBatchMapDto2;

    private List<UserRoleProgramBatchMap> userRoleProgramBatchMapList;

    @BeforeEach
    public void setup() {
        mockUserRoleProgramBatchMap = setMockUserRoleProgramBatchMapAndDto();
    }

    public UserRoleProgramBatchMap setMockUserRoleProgramBatchMapAndDto() {
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        User user = new User("U02", "Steve", "Job", "",
                879123456L, "Idaho", "PST", "https://www.linkedin.com/in/Steve Job/",
                "Mechanical Engineering", "Computer Science Engineering", "", "H4",
                timestamp, timestamp);

        Role role = new Role("R02", "Staff", "LMS_Staff", timestamp, timestamp);

        Program program = new Program(2L, "DataScience", "DataScience batch",
                "Active", timestamp, timestamp);
        Program program1 = new Program(1L, "SDET", "Testing batch",
                "Active", timestamp, timestamp);

        Batch batch = new Batch(5, "02", "DataScience BATCH 02", "Active", program,
                4, timestamp, timestamp);
        Batch batch1 = new Batch(1, "01", "SDET BATCH 01", "Active", program,
                5, timestamp, timestamp);

        mockUserRoleProgramBatchMap = new UserRoleProgramBatchMap(549L,
                user, role, program, batch, "Active",
                timestamp, timestamp);
        mockUserRoleProgramBatchMap2 = new UserRoleProgramBatchMap(550L,
                user, role, program1, batch1, "Active",
                timestamp, timestamp);

        mockUserRoleProgramBatchMapDto = new UserRoleProgramBatchMapDto("U02", "R02",
                                            2L,5,"Active");
        mockUserRoleProgramBatchMapDto2 = new UserRoleProgramBatchMapDto("U02", "R02",
                3L,3,"Active");

        return mockUserRoleProgramBatchMap;
    }


    @DisplayName("test to get Assigned Program/Batch(es) for All users")
    @Test
    void testGetAllUserRoleProgramBatchMaps() {
        //given
        List<UserRoleProgramBatchMap> userRoleProgramBatchMapList = new ArrayList<>();
        userRoleProgramBatchMapList.add(mockUserRoleProgramBatchMap);
        userRoleProgramBatchMapList.add(mockUserRoleProgramBatchMap2);

        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtoList = new ArrayList<>();
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto);
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto2);

        given(userRoleProgramBatchMapRepository.findAll()).willReturn(userRoleProgramBatchMapList);
        given(userRoleProgramBatchMapMapper.toUserRoleProgramBatchMapDtoList(userRoleProgramBatchMapList))
                .willReturn(userRoleProgramBatchMapDtoList);

        //when
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos = (userRoleProgramBatchMapService.getAllUserRoleProgramBatchMaps());

        //then
        assertThat(userRoleProgramBatchMapDtos).isNotNull();
        assertThat(userRoleProgramBatchMapDtos.size()).isEqualTo(2);

    }


    @DisplayName("test to get Assigned Program/Batch(es) for All users when no mapping is available")
    @Test
    void testGetAllUserRoleProgramBatchMapsWhenListIsEmpty() {
        //given
        given(userRoleProgramBatchMapRepository.findAll()).willReturn(Collections.emptyList());

        //when
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos =
                userRoleProgramBatchMapService.getAllUserRoleProgramBatchMaps();

        //then
        assertThat(userRoleProgramBatchMapDtos).isEmpty();
        assertThat(userRoleProgramBatchMapDtos.size()).isEqualTo(0);
    }


    @DisplayName("test to get Program/Batch assigned to a user by user id")
    @Test
    void testGetByUserId()
    {
        //given
        List<UserRoleProgramBatchMap> userRoleProgramBatchMapList = new ArrayList<>();
        userRoleProgramBatchMapList.add(mockUserRoleProgramBatchMap);
        userRoleProgramBatchMapList.add(mockUserRoleProgramBatchMap2);

        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtoList = new ArrayList<>();
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto);
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto2);

        given(userRoleProgramBatchMapRepository.
                findByUser_UserId(mockUserRoleProgramBatchMap.getUser().getUserId()))
                .willReturn(userRoleProgramBatchMapList);
        given(userRoleProgramBatchMapMapper.toUserRoleProgramBatchMapDtoList(userRoleProgramBatchMapList))
                .willReturn(userRoleProgramBatchMapDtoList);

        //when
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos = userRoleProgramBatchMapService.
                getByUserId(mockUserRoleProgramBatchMap.getUser().getUserId());

        //then
        assertThat(userRoleProgramBatchMapDtos).isNotNull();
    }


    @DisplayName("test to get Progran/Batch assigned to a User By User Id")
    @Test
    void testGetByUserIdNotFound()
    {
        //given
        given(userRoleProgramBatchMapRepository.findByUser_UserId
                (mockUserRoleProgramBatchMap.getUser().getUserId())).willReturn(Collections.emptyList());

        //when
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> userRoleProgramBatchMapService.getByUserId(mockUserRoleProgramBatchMap.getUser().getUserId()));

        //then
        Mockito.verify(userRoleProgramBatchMapMapper, never()).toUserRoleProgramBatchMapDtoList(any(List.class));

    }

    @DisplayName("test for delete all mappings By UserId")
    @Test
    void testdeleteAllByUserId() {
        //given
        List<UserRoleProgramBatchMap> userRoleProgramBatchMapList = new ArrayList<>();
        userRoleProgramBatchMapList.add(mockUserRoleProgramBatchMap);
        userRoleProgramBatchMapList.add(mockUserRoleProgramBatchMap2);


        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtoList = new ArrayList<>();
        mockUserRoleProgramBatchMapDto.setUserRoleProgramBatchStatus("Inactive");
        mockUserRoleProgramBatchMapDto2.setUserRoleProgramBatchStatus("Inactive");

        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto);
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto2);

        given(userRoleProgramBatchMapRepository.findByUser_UserId(mockUserRoleProgramBatchMap.
                                    getUser().getUserId())).willReturn(userRoleProgramBatchMapList);
        //given(userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap)).willReturn(mockUserRoleProgramBatchMap);

        //when
        userRoleProgramBatchMapService.deleteAllByUserId(mockUserRoleProgramBatchMap.getUser().getUserId());

        //then
        assertThat(mockUserRoleProgramBatchMapDto).isNotNull();
        assertThat(mockUserRoleProgramBatchMapDto.getUserRoleProgramBatchStatus()).isEqualTo("Inactive");
    }

    @DisplayName("test for delete all mappings By User Id whose Id is not found")
    @Test
    void testDeleteAllByUserIdNotFound() {
        //given
        given(userRoleProgramBatchMapRepository.findByUser_UserId(mockUserRoleProgramBatchMap.getUser().getUserId()))
                .willReturn(Collections.emptyList());

        //when
        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> userRoleProgramBatchMapService.deleteAllByUserId(mockUserRoleProgramBatchMap.getUser().getUserId()));

        //then
        Mockito.verify(userRoleProgramBatchMapMapper, never()).toUserRoleProgramBatchMapDtoList(any(List.class));


    }


}