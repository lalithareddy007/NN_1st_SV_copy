package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserPictureEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Timestamp;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.assertNotNull;



@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class UserPictureRepositoryTest {
    @Autowired
    private UserPictureRepository userPictureRepo;

    private UserPictureEntity mockUserPictureEntity;
    private User mockUser;
    private UserRepository userRepo;

    @BeforeEach
    public void setUp() {
        mockUserPictureEntity = setMockUserPictureEntity();
    }

    private UserPictureEntity  setMockUserPictureEntity() {
        mockUser = new User("U01", "John1", "K", " ", 1234123457L, "USA", "EST", "www.linkedin.com/John",
                "MS", "MBA", "Professor", "GC", Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));
        UserPictureEntity  userPictureEntity= new UserPictureEntity(2L, "Resume", mockUser, "/path/to/picture1.jpg");
        return userPictureEntity ;
    }

    @Test
    @DisplayName("Test get User picture by user Id and User File Type")
    public void testFindByUserAndUserFileType() {

        // given
        userPictureRepo.save(mockUserPictureEntity);

        // when
        UserPictureEntity result = userPictureRepo.findByuserAnduserFileType(mockUser.getUserId(), mockUserPictureEntity.getUserFileType());

        // then
        assertNotNull(result);
    }
}
