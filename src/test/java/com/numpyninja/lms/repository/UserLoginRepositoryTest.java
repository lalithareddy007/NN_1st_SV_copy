package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserLogin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class UserLoginRepositoryTest {

    @Autowired
    private UserLoginRepository userLoginRepository;

    private UserLogin mockUserLogin;

    @BeforeEach
    public void setUp(){
        setMockUserLoginAndSave();
    }

    private void setMockUserLoginAndSave() {
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);
        User user = new User("U01", "Steve", "Jobs", "", (long) 1234567890, "CA", "PST", "@stevejobs",
                "", "", "", "H4", timestamp, timestamp);
        mockUserLogin=new UserLogin("U01","test@gmail.com","test","Active",timestamp,timestamp,user);
        //userLoginRepository.save(mockUserLogin);
    }


    @DisplayName("JUnit test for fetch Users by EmailAddress")
    @Test
    void givenEmailAddress_WhenFindUserLogin_ReturnUserLoginObjects(){
        //given
        userLoginRepository.save(mockUserLogin);
        //when
        Optional<UserLogin> userLoginDet=userLoginRepository.findByUserLoginEmailIgnoreCase(mockUserLogin.getUserLoginEmail());
        //then
        assertThat(userLoginDet).isNotEmpty();
    }

    @DisplayName("JUnit test for fetch User by UserId")
    @Test
    void givenUserId_WhenFindUserLogin_ReturnUserLoginObject(){
        //given
        userLoginRepository.save(mockUserLogin);
        //when
        Optional<UserLogin> userLogin=userLoginRepository.findByUserUserId(mockUserLogin.getUserId());
        //then
        assertThat(userLogin).isNotEmpty();
    }

    @DisplayName("Junit test for updating user login")
    @Test
    void shouldUpdateUserLogin(){
        //given
        userLoginRepository.save(mockUserLogin);
        String userId="U01";
        String userEmailToUpdate = "testUpdate@gmail.com";
        String userLoginStatusToUpdate="INACTIVE";
        //when
        userLoginRepository.updateUserLogin(userId,userEmailToUpdate,userLoginStatusToUpdate);
        //then
        Optional<UserLogin> userLogin=userLoginRepository.findByUserUserId(mockUserLogin.getUserId());
        assertThat(userLogin.get().getUserLoginEmail()).isEqualTo(userEmailToUpdate);
        assertThat(userLogin.get().getLoginStatus()).isEqualTo(userLoginStatusToUpdate);
    }
}
