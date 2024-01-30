package com.numpyninja.lms.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserSkillRepositoryTest {
    @Autowired
    private UserSkillRepository userSkillRepository;


    @DisplayName("Test for get UserSkill by UserId")
    @Test
    void testFindUserSKillByUserId(){
        //given
        var userId = userSkillRepository.findAll().get(0).getUser().getUserId();
        //when
        var userSkill1=userSkillRepository.findByUserId(userId);
        //then
        assertThat(userSkill1).isNotNull();
        assertThat(userSkill1.size()).isGreaterThan(0);
    }

    @DisplayName("Test for get UserSkill by User")
    @Test
    void testFindUserSkillByUser(){
        //given
        var user = userSkillRepository.findAll().get(0).getUser();
        //when
        var userSkill1=userSkillRepository.findByUser(user);
        //then
        assertThat(userSkill1).isNotNull();
        assertThat(userSkill1.size()).isGreaterThan(0);
    }
    @DisplayName("Test for Exist By by UserId")
    @Test
    void testExistsByUserId(){
        //given
        var userId = userSkillRepository.findAll().get(0).getUser().getUserId();
        //when
        var userSkill1=userSkillRepository.existsByUserId(userId);
        //then
        assertThat(userSkill1).isNotNull();
        assertThat(userSkill1.size()).isGreaterThan(0);
    }


}
