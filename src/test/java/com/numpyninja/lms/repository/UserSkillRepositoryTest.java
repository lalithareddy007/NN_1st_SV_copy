package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.SkillMaster;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserSkill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class UserSkillRepositoryTest {
    @Autowired
    private UserSkillRepository userSkillRepository;
    private User user;
    private SkillMaster skillMaster;

    private UserSkill mockUserSkill;

    @BeforeEach
    public void setup() {
        mockUserSkill = setMockUserSkill();
    }


    public UserSkill setMockUserSkill(){
        user = new User("U10", "Steve", "Jobs", "Martin",
                1234567890L, "CA", "PST", "@stevejobs", "",
                "", "", "Citizen", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
         skillMaster = new SkillMaster(2L, "SQL", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
       UserSkill userSkill1 = new UserSkill("US10", user, skillMaster, 24, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        return userSkill1;
    }

    @DisplayName("Test for get UserSkill by UserId")
    @Test
    public void testFindUserSKillByUserId(){
        //given
        userSkillRepository.save(mockUserSkill);
        //when
        List<UserSkill> userSkill1=userSkillRepository.findByUserId(mockUserSkill.getUser().getUserId());
        //then
        assertThat(userSkill1).isNotNull();
        assertThat(userSkill1.size()).isGreaterThan(0);
    }

    @DisplayName("Test for get UserSkill by User")
    @Test
    public void testFindUserSkillByUser(){
        //given
        userSkillRepository.save(mockUserSkill);
        //when
        List<UserSkill> userSkill1=userSkillRepository.findByUser(mockUserSkill.getUser());
        //then
        assertThat(userSkill1).isNotNull();
        assertThat(userSkill1.size()).isGreaterThan(0);
    }
    @DisplayName("Test for Exist By by UserId")
    @Test
    public void testExistsByUserId(){
        //given
        userSkillRepository.save(mockUserSkill);
        //when
        List<UserSkill> userSkill1=userSkillRepository.existsByUserId(mockUserSkill.getUser().getUserId());
        //then
        assertThat(userSkill1).isNotNull();
        assertThat(userSkill1.size()).isGreaterThan(0);
    }


}
