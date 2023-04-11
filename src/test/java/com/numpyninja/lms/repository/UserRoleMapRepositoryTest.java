package com.numpyninja.lms.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.numpyninja.lms.dto.UserAndRoleDTO;
import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.dto.UserRoleMapSlimDTO;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Role;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleMap;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class UserRoleMapRepositoryTest {

	
	@Autowired
	UserRoleMapRepository userRoleMapRepo;
	
	@Autowired
	UserRepository userRepo;
	
	private UserRoleMap userRoleMap;
	private User mockUser;
	private UserAndRoleDTO mockUserAndRoleDto;
	private UserRoleMap mockUserRoleMap;

	@BeforeEach
	public void setup() {
		userRoleMap = mockUserRoleMap();
	
	}
	
	private UserRoleMap mockUserRoleMap() {

		Date utilDate = new Date();
		Long userRoleId = 10L;

		User user = new User("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST", "www.linkedin.com/Kalam1234",
				"MCA", "MBA", "Indian scientist", "H4", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));

		Role role = new Role("R03", "User", "LMS_User", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));

		String userRoleStatus = "Active";

		mockUserRoleMap = new UserRoleMap(userRoleId, user, role, userRoleStatus,
				new Timestamp(utilDate.getTime()), new Timestamp(utilDate.getTime()));
				
		return mockUserRoleMap;

	}

	@Test
	@DisplayName("To test the method find user role map by given user ID")
	public void testfindUserRoleMapsByUserUserId() {

		
		userRepo.save(userRoleMap.getUser());
		userRoleMapRepo.save(userRoleMap);
		
		String userId = userRoleMap.getUser().getUserId();
		List<UserRoleMap> userRole = userRoleMapRepo.findUserRoleMapsByUserUserId(userId);
		
		assertThat(userRole).isNotNull();
		assertThat(userRole.get(0).getUser().getUserId()).isEqualTo(userId);
	}
	
	

}
