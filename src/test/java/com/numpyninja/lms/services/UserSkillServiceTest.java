package com.numpyninja.lms.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.UserSkillDTO;
import com.numpyninja.lms.entity.SkillMaster;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserSkill;
import com.numpyninja.lms.mappers.UserSkillMapper;
import com.numpyninja.lms.repository.SkillMasterRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserSkillRepository;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSkillServiceTest {

    @InjectMocks
    private UserSkillService userSkillService;
    @Mock
    private UserSkillRepository userSkillRepository;
    @Mock
    private UserSkillMapper userSkillMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillMasterRepository skillMasterRepository;
   // @Mock
    //private UserSkillDTO userSkillDTO;
    private User user;
    private UserSkill userSkill1,userSkill2;
    private UserSkillDTO userSkillDTO1,userSkillDTO2;
    List<UserSkill> userSkillList;
    List<UserSkillDTO> userSkillDTOList;

    @BeforeEach
    public void setUp(){
        setMockUserSkillAndDTO();
    }
    @SneakyThrows
    private void setMockUserSkillAndDTO() {
      // User user1 = new User("U10", "Abdul", "Kalam", "M", 2222222222L, "India", "IST", "www.linkedin.com/Kalam1234",
                //"MCA", "MBA", "Indian scientist", "H4", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
         user =new User("U10", "Steve", "Jobs", "Martin",
                1234567890L, "CA", "PST", "@stevejobs", "",
                "", "", "Citizen", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        SkillMaster skillMaster = new SkillMaster(2L, "SQL", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
       // User user1 = new User();
        //user1.setUserId("U10");

        userSkill1 = new UserSkill("US10", user, skillMaster, 24, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        userSkillDTO1 = new UserSkillDTO("US23", "U10", 6, "Python", 24);
        userSkill2 = new UserSkill("US21", user, skillMaster, 23, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        userSkillDTO2 = new UserSkillDTO("US11", "U02", 7, "Java", 44);
        userSkillList = new ArrayList<>();
        userSkillDTOList = new ArrayList<>();

    }
    @DisplayName("Test for Getting all UserSkill")
    @SneakyThrows
    @Test
    public void getAllUserSkillServiceTest(){
        //given
        userSkillList.add(userSkill1);
        userSkillList.add(userSkill2);
        userSkillDTOList.add(userSkillDTO1);
        userSkillDTOList.add(userSkillDTO2);
        when(userSkillRepository.findAll()).thenReturn(userSkillList);
        when(userSkillMapper.toUserSkillDTOList(userSkillList)).thenReturn(userSkillDTOList);
        //when
        List<UserSkillDTO>  userSkillDTOS = userSkillService.getAllUserSkills();
        //then
        assertThat(userSkillDTOS.size()).isGreaterThan(0);
        verify(userSkillRepository).findAll();
        verify(userSkillMapper).toUserSkillDTOList(userSkillList);
    }
    @DisplayName("Test for getting all UserSkills for particular User")
    @Test
    @SneakyThrows
    void getUserSkillForUserServiceTest() {
        //given
        String userId = "U10";
        userSkillList.add(userSkill1);
        userSkillList.add(userSkill2);
        userSkillDTOList.add(userSkillDTO1);
        userSkillDTOList.add(userSkillDTO2);
        when(userSkillRepository.findByUserId(userId)).thenReturn(userSkillList);
        when(userSkillMapper.toUserSkillDTOList(userSkillList)).thenReturn(userSkillDTOList);
        //when
        List<UserSkillDTO> userSkillDTOS1 = userSkillService.getUserSkillForUser(user.getUserId());
        //then
        assertThat(userSkillDTOS1.size()).isGreaterThan(0);
       verify(userSkillRepository).findByUserId(userId);
        verify(userSkillMapper).toUserSkillDTOList(userSkillList);

    }
    @DisplayName("Test for creating UserSkill")
    @SneakyThrows
    @Test
    public void CreateUserSkillServiceTest(){
        when(userSkillMapper.toUserSkillEntity(userSkillDTO1)).thenReturn(userSkill1);
        when(userSkillRepository.findByUser(userSkill1.getUser())).thenReturn(Collections.emptyList());
        when(userSkillRepository.save(userSkill1)).thenReturn(userSkill1);
        UserSkillDTO userSkillDTO =userSkillService.createUserSkill(userSkillDTO1);
        assertThat(userSkillDTO).isNotNull();
        verify(userSkillMapper).toUserSkillEntity(userSkillDTO);
        verify(userSkillRepository).findByUser(userSkill1.getUser());
        verify(userSkillRepository).save(userSkill1);

    }

}
