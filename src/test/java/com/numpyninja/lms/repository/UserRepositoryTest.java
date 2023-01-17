package com.numpyninja.lms.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.User;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	UserRepository userRepo;

	private User mockUser;
	private User mockUser1;
	

	@BeforeEach
	public void setup() {
		mockUser = setMockUser();
	}

	private User setMockUser() {

		Date utilDate = new Date();
		mockUser = new User("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST", "www.linkedin.com/Kalam1234",
				"MCA", "MBA", "Indian scientist", "H4", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));
		
		mockUser1 = new User("U03", "Homi", "Baba", "J", 1122112211L, "India", "IST", "www.linkedin.com/Homi1234",
				"MCA", "MBA", "Indian scientist", "H1B", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));
		return mockUser;
	}

	 
	  @DisplayName("test for getting user By Id")
	  @Test
	  public void testfindUserById() {
	  
		  userRepo.save(mockUser);
		  Optional<User> findUser = userRepo.findById(mockUser.getUserId());
		  assertThat(findUser).isNotNull();
		  assertThat(findUser.get().getUserFirstName()).isEqualTo(mockUser.getUserFirstName());
	  }
	  
	  @DisplayName("test for getting all users")
	  @Test
	  public void testfindAllUsers() {
	  
		  userRepo.save(mockUser);
		  userRepo.save(mockUser1);
		  List<User> findAllUsers = userRepo.findAll();

		  assertThat(findAllUsers).isNotNull();
		  assertThat(findAllUsers).hasSizeGreaterThan(0);

	  }
	  
	  //userRepository.deleteById
	  @DisplayName("test for deleting user by Id")
	  @Test
	  public void testDeleteUsersById() {
		  
		  	userRepo.save(mockUser);
		  	String userId = "U02";

			User user = userRepo.findById(userId).get();
			userRepo.deleteById(userId);
			Optional<User> userCheck = userRepo.findById(userId);

			assertThat(userCheck).isEmpty();
		  
	  }

}
