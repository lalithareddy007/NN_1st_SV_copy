package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.UserLogin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserLoginRepositoryTest {

    @Autowired
    private UserLoginRepository userLoginRepository;


    @DisplayName("JUnit test for fetch Users by EmailAddress")
    @Test
    void givenEmailAddress_WhenFindUserLogin_ReturnUserLoginObjects(){
        //given
        var userEmail = userLoginRepository.findAll().get(0).getUserLoginEmail();
        //when
        Optional<UserLogin> userLoginDet=userLoginRepository.findByUserLoginEmailIgnoreCase(userEmail);
        //then
        assertThat(userLoginDet).isNotEmpty();
    }

    @DisplayName("JUnit test for fetch User by UserId")
    @Test
    void givenUserId_WhenFindUserLogin_ReturnUserLoginObject(){
        //given
        var userId = userLoginRepository.findAll().get(0).getUserId();
        //when
        Optional<UserLogin> userLogin=userLoginRepository.findByUserUserId(userId);
        //then
        assertThat(userLogin).isNotEmpty();
    }

    @DisplayName("Junit test for updating user login")
    @Test
    void shouldUpdateUserLogin(){
        //given
        var userLogin = userLoginRepository.findAll().get(0);
        userLogin.setUserLoginEmail("12345@gmail.com");
        var userId = userLogin.getUser().getUserId();
        var userEmailToUpdate = userLogin.getUserLoginEmail();
        var userLoginStatusToUpdate = userLogin.getLoginStatus();
        //when
        userLoginRepository.updateUserLogin(userId,userEmailToUpdate,userLoginStatusToUpdate);
        //then
        Optional<UserLogin> userLogin1 =userLoginRepository.findByUserUserId(userId);
        assertThat(userLogin1.get().getUserLoginEmail()).isEqualTo(userEmailToUpdate);
        assertThat(userLogin1.get().getLoginStatus()).isEqualTo(userLoginStatusToUpdate);
    }
}
