package com.numpyninja.lms.repository;

import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserFileEntity;

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
public class UserFileRepositoryTest {
	@Autowired
	private UserFileRepository userFileRepo;

	@Autowired
	private UserRepository userRepo;

	private UserFileEntity mockUserFileEntity;
	private User mockUser;

	@BeforeEach
	public void setUp() {
		setMockUserFileEntity();
	}

	private void setMockUserFileEntity() {
		// Insert a user record
		mockUser = new User();
		mockUser.setUserFirstName("John1");
		mockUser.setUserLastName("K");
		mockUser.setUserPhoneNumber(1234123457L);
		mockUser.setUserLocation("USA");
		mockUser.setUserTimeZone("EST");
		mockUser.setUserLinkedinUrl("www.linkedin.com/John");
		mockUser.setUserEduUg("MS");
		mockUser.setUserEduPg("MBA");
		mockUser.setUserComments("");
		mockUser.setUserVisaStatus("US-Citizen");
		mockUser.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
		mockUser.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
		userRepo.save(mockUser);

		// Insert a File entity
		mockUserFileEntity = new UserFileEntity(1L, "Resume", mockUser, "C:/Document");
		userFileRepo.save(mockUserFileEntity);

	}

	@Test
	@DisplayName("Test get User File by user Id and User File Type")
	public void testFindByUserAndUserFileType() {

		// given
		String userId = mockUser.getUserId();
		String userFieldType = mockUserFileEntity.getUserFileType();
		// when
		UserFileEntity result = userFileRepo.findByuserAnduserFileType(userId, userFieldType);

		// then
		assertNotNull(result);
	}
}
