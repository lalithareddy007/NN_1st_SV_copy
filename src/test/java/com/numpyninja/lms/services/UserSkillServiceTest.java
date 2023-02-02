package com.numpyninja.lms.services;


import com.numpyninja.lms.dto.UserSkillDTO;
import com.numpyninja.lms.entity.SkillMaster;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserSkill;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserSkillMapper;
import com.numpyninja.lms.repository.SkillMasterRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserSkillRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

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
    private User user;
    private SkillMaster skillMaster;
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
         skillMaster = new SkillMaster(2L, "SQL", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
       // User user1 = new User();
        //user1.setUserId("U10");

        userSkill1 = new UserSkill("US10", user, skillMaster, 24, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        userSkillDTO1 = new UserSkillDTO("US23", "U10", 2, "Python", 24);
        userSkill2 = new UserSkill("US21", user, skillMaster, 23, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        userSkillDTO2 = new UserSkillDTO("US11", "U02", 3, "Java", 44);
        userSkillList = new ArrayList<>();
        userSkillDTOList = new ArrayList<>();

    }
    @DisplayName("Test for Getting all UserSkill")
    @SneakyThrows
    @Test
    public void testGetAllUserSkillService(){
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
    @DisplayName("test - GetUserSkillByUserId - When UserId is Not Found")
    @SneakyThrows
    @Test
    public void testGetUserSkillWhenUserIdIsNotFound() {
        //given
        String userId = "U08";
        given(userSkillRepository.findById(userId)).isEmpty();
        //when
        assertThrows(ResourceNotFoundException.class, () -> userSkillService.getUserSkillForUser(userId));

        //then
        Mockito.verify(userSkillRepository).findById(userId);
    }

    @DisplayName("Test for getting all UserSkills for particular User")
    @Test
    @SneakyThrows
    void testGetUserSkillForUser_WhenUserIsPresent() {
        //given
        String userId = "U10";
        userSkillList.add(userSkill1);
        userSkillList.add(userSkill2);
        userSkillDTOList.add(userSkillDTO1);
        userSkillDTOList.add(userSkillDTO2);
        lenient().when(userSkillRepository.findByUserId(userId)).thenReturn(userSkillList);
        lenient().when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        lenient(). when(userSkillMapper.toUserSkillDTOList(userSkillList)).thenReturn(userSkillDTOList);
        //when
        List<UserSkillDTO> userSkillDTOS1 = userSkillService.getUserSkillForUser(user.getUserId());
        //then
        assertThat(userSkillDTOS1).isNotNull();

    }
    @DisplayName("Test for creating UserSkill")
    @SneakyThrows
    @Test
    public void testCreateUserSkill(){
        //given
       lenient().when(userSkillMapper.toUserSkillEntity(userSkillDTO1)).thenReturn(userSkill1);
        lenient().when(userSkillRepository.findByUser(userSkill1.getUser())).thenReturn(Collections.emptyList());
        lenient().when(userSkillRepository.save(userSkill1)).thenReturn(userSkill1);
        lenient().when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        lenient().when(skillMasterRepository.findById(skillMaster.getSkillId())).thenReturn(Optional.of(skillMaster));
        //when
        UserSkillDTO userSkillDTO =userSkillService.createUserSkill(userSkillDTO1);
        //then
        assertThat(userSkillDTO1).isNotNull();
    }
    @DisplayName("test for creating an userSkill with Duplicate UserId")
    @SneakyThrows
    @Test
    void testCreateUserSkillWithDuplicateId() {
        //given
        String userSkillId="US10";
        lenient().when(userSkillRepository.findById(userSkillId)).thenReturn(Optional.of(userSkill1));
        //when
        Assertions.assertThrows(ResourceNotFoundException.class, () -> userSkillService.createUserSkill(userSkillDTO1));
        //then
        Mockito.verify(userSkillRepository, never()).save(any(UserSkill.class));
        Mockito.verify(userSkillMapper, never()).toUserSkillDTO(any(UserSkill.class));

    }

    @DisplayName("Test for updating UserSkill")
    @SneakyThrows
    @Test
    public void testUpdateUserSkill(){
        //given
        String userSkillId="US10";
        int monthsOfExpiry = 12;
        UserSkillDTO updatedUserSkillDTO = userSkillDTO1;
        UserSkill newUserSkill = userSkill1;
        newUserSkill.setMonths(monthsOfExpiry);

        lenient().when(userSkillMapper.toUserSkillEntity(userSkillDTO1)).thenReturn(userSkill1);
        lenient().when(userSkillRepository.findById(userSkillId)).thenReturn(Optional.of(userSkill1));

        when(userSkillRepository.save(userSkill1)).thenReturn(newUserSkill);
        when(userSkillMapper.toUserSkillDTO(userSkill1)).thenReturn(updatedUserSkillDTO);
        updatedUserSkillDTO.setMonths(monthsOfExpiry);
        //when
        UserSkillDTO userSkillDTO=userSkillService.updateUserSkill(updatedUserSkillDTO,userSkillId);
        //then
        assertThat(userSkillDTO).isNotNull();
        assertThat(userSkillDTO.getMonths()).isEqualTo(12);
    }
    @DisplayName("test for updating an userSkill whose Id is not found")
    @SneakyThrows
    @Test
    void testUpdateUserSkillWhoseIdIsNotFound() {
        //given
        String userSkillId="US10";
        BDDMockito.given(userSkillRepository.findById(userSkillId)).willReturn(Optional.empty());
        //when
        Assertions.assertThrows(ResourceNotFoundException.class, () -> userSkillService.updateUserSkill(userSkillDTO1, userSkillId));
        //then
        Mockito.verify(userSkillMapper, never()).toUserSkillEntity(any(UserSkillDTO.class));
        Mockito.verify(userSkillRepository, never()).save(any(UserSkill.class));
        Mockito.verify(userSkillMapper, never()).toUserSkillDTO(any(UserSkill.class));
    }

    @DisplayName("Test for Deleting UserSkill by UserSkillId")
    @SneakyThrows
    @Test
    public void testDeleteUserSkillByUserSkillId(){
        //given
        String userSkillId="US10";
        when(userSkillRepository.findById(userSkillId)).thenReturn(Optional.of(userSkill1));
        willDoNothing().given(userSkillRepository).deleteById(userSkillId);
        //when
        userSkillService.deleteUserSkillByUserSkillId(userSkillId);
        verify(userSkillRepository).findById(userSkillId);
        verify(userSkillRepository).deleteById(userSkillId);
    }
    @DisplayName("test - When UserSkill ID is Null")
    @SneakyThrows
    @Test
    public void testDeleteUserSkillByIdWhenIdIsNull() {
        String userSkillId="US10";
       BDDMockito.given(userSkillRepository.findById(userSkillId)).willReturn(Optional.empty());
       //when
        Assertions.assertThrows(ResourceNotFoundException.class, () -> userSkillService.deleteUserSkillByUserSkillId(userSkillId));
        //then
        Mockito.verify(userSkillRepository, never()).deleteById(any(String.class));
    }

  /*  @DisplayName("Test for Deleting UserSkill by UserId")
    @SneakyThrows
    @Test
    public void testDeleteUserSkillByUserId(){
        //given
       // ArrayList arr = new ArrayList<>();
        //User user =new User();
        user.setUserId("U08");
       //arr.add(user);
        lenient().when(userSkillRepository.existsByUserId(user.getUserId())).thenReturn(userSkillList);
        //when(userSkillRepository.existsByUserId(user.getUserId())).thenReturn(Collections.emptyList());
        lenient().when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        willDoNothing().given(userSkillRepository).deleteByUser(user);
        userSkillService.deleteUserByUserId(user.getUserId());
        verify(userSkillRepository).getByUserId(user.getUserId());
 }*/

}
