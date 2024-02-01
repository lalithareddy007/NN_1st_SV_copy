package com.numpyninja.lms.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRoleMapRepositoryTest {

	@Autowired
	UserRoleMapRepository userRoleMapRepo;


	@Test
	@DisplayName("To test the method find user role map by given user ID")
	void testFindUserRoleMapsByUserUserId() {
		//given
		var userId = userRoleMapRepo.findAll().get(0).getUser().getUserId();
		//when
		var userRole = userRoleMapRepo.findUserRoleMapsByUserUserId(userId);
		//then
		assertThat(userRole).isNotNull();
		assertThat(userRole.get(0).getUser().getUserId()).isEqualTo(userId);
	}
	
	

}
