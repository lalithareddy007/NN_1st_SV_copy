package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.UserRoleProgramBatchMapDto;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.mappers.UserRoleProgramBatchMapMapper;
import com.numpyninja.lms.repository.UserRoleProgramBatchMapRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserRoleProgramBatchMapServiceTest {

    @InjectMocks
    private UserRoleProgramBatchMapService userRoleProgramBatchMapService;

    @Mock
    private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;

    @Mock
    private UserRoleProgramBatchMapMapper userRoleProgramBatchMapMapper;

    private UserRoleProgramBatchMap mockUserRoleProgramBatchMap,mockUserRoleProgramBatchMap2;

    private UserRoleProgramBatchMapDto mockUserRoleProgramBatchMapDto;

    private List<UserRoleProgramBatchMap> userRoleProgramBatchMaps;

    @BeforeEach
    public void setup() {
        mockUserRoleProgramBatchMap = setMockUserRoleProgramBatchMapAndDto();
    }

    public UserRoleProgramBatchMap setMockUserRoleProgramBatchMapAndDto()
    {
        LocalDateTime now= LocalDateTime.now();
        Timestamp timestamp= Timestamp.valueOf(now);

        User user = new User("U02", "Steve", "Job", "",
                879123456L, "Idaho", "PST", "https://www.linkedin.com/in/Steve Job/",
                "Mechanical Engineering","Computer Science Engineering", "", "H4",
                timestamp, timestamp);

        Role role = new Role("R02", "Staff", "LMS_Staff", timestamp, timestamp);

        Program program = new Program(2L,"DataScience","DataScience batch",
                "Active", timestamp, timestamp);
        Program program1 = new Program(1L,"SDET","Testing batch",
                "Active", timestamp, timestamp);

        Batch batch = new Batch(5, "02", "DataScience BATCH 02", "Active", program,
                4, timestamp, timestamp);
        Batch batch1 = new Batch(1, "01", "SDET BATCH 01", "Active", program,
                5, timestamp, timestamp);

        mockUserRoleProgramBatchMap = new UserRoleProgramBatchMap(549L,
                                         user,role,program,batch,"Active",
                                        timestamp,timestamp);
        mockUserRoleProgramBatchMap2 = new UserRoleProgramBatchMap(550L,
                                     user,role,program1,batch1,"Active",
                                        timestamp,timestamp);

        mockUserRoleProgramBatchMapDto = new UserRoleProgramBatchMapDto("U02","R02",
                                        2L,5,"Active");

        return mockUserRoleProgramBatchMap;
    }


    @DisplayName("")
    @Test
    void testGetAllUserRoleProgramBatchMaps()
    {
        //given
        UserRoleProgramBatchMap mockUserRoleProgramBatchMap2 = setMockUserRoleProgramBatchMapAndDto();
        mockUserRoleProgramBatchMap2.setProgram(mockUserRoleProgramBatchMap2.getProgram());
        mockUserRoleProgramBatchMap2.setBatch(mockUserRoleProgramBatchMap2.getBatch());
        List<UserRoleProgramBatchMap> userRoleProgramBatchMapList = new ArrayList<>();
        userRoleProgramBatchMapList.add(mockUserRoleProgramBatchMap);
        userRoleProgramBatchMapList.add(mockUserRoleProgramBatchMap2);

        UserRoleProgramBatchMapDto mockUserRoleProgramBatchMapDto2 = mockUserRoleProgramBatchMapDto;
        mockUserRoleProgramBatchMapDto2.setProgramId(1L);
        mockUserRoleProgramBatchMapDto2.setBatchId(1);
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtoList = new ArrayList<>();
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto);
        userRoleProgramBatchMapDtoList.add(mockUserRoleProgramBatchMapDto2);

        given(userRoleProgramBatchMapRepository.findAll()).willReturn(userRoleProgramBatchMapList);
        given(userRoleProgramBatchMapMapper.toUserRoleProgramBatchMapDtoList(userRoleProgramBatchMapList));

        //when
        List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos = (userRoleProgramBatchMapService.getAllUserRoleProgramBatchMaps());

        //then
        assertThat(userRoleProgramBatchMapDtos).isNotNull();
        assertThat(userRoleProgramBatchMapDtos.size()).isEqualTo(2);
    }
}
