package com.numpyninja.lms.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserPictureRepositoryTest {
	@Autowired
	private UserPictureRepository userPictureRepo;
	@Test
	@DisplayName("Test get User picture by user Id and User File Type")
	void testFindByUserAndUserFileType() {

		// given
		var userPictureEntity = userPictureRepo.findAll().get(0);
		var userId = userPictureEntity.getUser().getUserId();
		var userFieldType = userPictureEntity.getUserFileType();
		// when
		var result = userPictureRepo.findByuserAnduserFileType(userId, userFieldType);

		// then
		assertNotNull(result);
	}
}
