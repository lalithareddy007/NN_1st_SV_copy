package com.numpyninja.lms.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepo;

	  @DisplayName("test for getting user By Id")
	  @Test
	  void testFindUserById() {
	  
		  var user = userRepo.findAll().get(0);
		  var userId = user.getUserId();
		  var userFirstName = user.getUserFirstName();
		  var findUser = userRepo.findById(userId);
		  assertThat(findUser).isNotNull();
		  assertThat(findUser.get().getUserFirstName()).isEqualTo(userFirstName);
	  }
	  
	  @DisplayName("test for getting all users")
	  @Test
	  void testFindAllUsers() {

		  var findAllUsers = userRepo.findAll();

		  assertThat(findAllUsers).isNotNull().hasSizeGreaterThan(0);

	  }

	  @DisplayName("test for deleting user by Id")
	  @Test
	  void testDeleteUsersById() {

		  var userId = userRepo.findAll().get(0).getUserId();
		  userRepo.deleteById(userId);
		  var userCheck = userRepo.findById(userId);
		  assertThat(userCheck).isEmpty();
		  
	  }

}
