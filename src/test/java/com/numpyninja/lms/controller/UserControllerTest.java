package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.dto.*;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Role;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleMap;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserMapper;
import com.numpyninja.lms.services.UserServices;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

	@MockBean
	private UserServices userService;

	@MockBean
	private UserMapper userMapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private UserDto mockUserDto;

	private UserRoleMap mockUserRoleMap;

	private UserAndRoleDTO mockUserAndRoleDto;

	private UserRoleMapSlimDTO mockUserRoleMapSlimDto;

	private UserRoleProgramBatchDto mockUserRoleProgramBatchDto, mockUserRoleProgramBatchDto2;

	@BeforeEach
	public void setup() {
		setMockUserAndDto();
	}

	private void setMockUserAndDto() {
		// mockUser = new User("U01","Srinivasa", "Ramanujan"," ", 2323232323L, "India",
		// "IST", "www.linkedin.com/Ramanujan1234","MCA","MBA","Indian scientist","H1B",null, null);
		mockUserDto = new UserDto("U01", "Srinivasa", "Ramanujan", " ", 2323232323L, "India", "IST",
				"www.linkedin.com/Ramanujan1234", "MCA", "MBA", "Indian scientist", "H1B");

		Date utilDate = new Date();
		Long userRoleId = 10L;

		User user = new User("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST", "www.linkedin.com/Kalam1234",
				"MCA", "MBA", "Indian scientist", "H4", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));

		Role role = new Role("R03", "User", "LMS_User", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));

		Set<Batch> batches = new HashSet<Batch>();

		String userRoleStatus = "Active";

		mockUserRoleMap = new UserRoleMap(userRoleId, user, role, batches, userRoleStatus,
				new Timestamp(utilDate.getTime()), new Timestamp(utilDate.getTime()));

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches =
				List.of(new UserRoleProgramBatchSlimDto(2, "Active"));
		mockUserRoleProgramBatchDto = UserRoleProgramBatchDto.builder().roleId("R03").programId(2L)
				.userRoleProgramBatches(mockUserRoleProgramBatches).build();

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches2 =
				List.of(new UserRoleProgramBatchSlimDto(2, "Active"),
						new UserRoleProgramBatchSlimDto(5, "Active"));
		mockUserRoleProgramBatchDto2 = UserRoleProgramBatchDto.builder().roleId("R02").programId(2L)
				.userRoleProgramBatches(mockUserRoleProgramBatches).build();

	}

	@Test
	@DisplayName("test to get all the users")
	void testgetAllUsers() throws Exception {

		UserDto mockUserDto2 = new UserDto("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST",
				"www.linkedin.com/Kalam1234", "MCA", "MBA", "Indian scientist", "H4");

		ArrayList<UserDto> userDtoList = new ArrayList();
		userDtoList.add(mockUserDto);
		userDtoList.add(mockUserDto2);
		System.out.println("userDtoList " + userDtoList);

		when(userService.getAllUsers()).thenReturn(userDtoList);

		mockMvc.perform(get("/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(userDtoList.size()))
				.andDo(print());
	}

	@Test
	@DisplayName("test to get user Info for a given user ID")
	void testgetUserInfoById() throws Exception {

		ArrayList<UserRoleMap> userRoleMapList = new ArrayList();
		userRoleMapList.add(mockUserRoleMap);
		String userId = "U02";
		given(userService.getUserInfoById(userId)).willReturn(userRoleMapList);

		ResultActions response = mockMvc.perform(get("/users/{id}", userId));

		response.andExpect(status().isOk())
				.andDo(print())
				.andExpect(jsonPath("$..userRoleStatus")
						.value("Active"))
				.andExpect(jsonPath("$..user.userId")
						.value("U02"))
				.andExpect(jsonPath("$..role.roleId")
						.value("R03"))
				.andExpect(jsonPath("$..user.userFirstName").value("Abdul"))
				.andExpect(jsonPath("$", hasSize(userRoleMapList.size())));
	}



	@Test
	@DisplayName("test to create user with their role ")
	void testcreateUserWithRole() throws Exception{

		UserRoleMapSlimDTO mockUserRoleMapSlimDto1 = new UserRoleMapSlimDTO("RO2","Active");
		UserRoleMapSlimDTO mockUserRoleMapSlimDto2 = new UserRoleMapSlimDTO("RO3","Active");

		UserDto mockUserDto1 = new UserDto();

		List<UserRoleMapSlimDTO> mockUserRoleMapSlimDtoList = new ArrayList<>();
		mockUserRoleMapSlimDtoList.add(mockUserRoleMapSlimDto1);
		mockUserRoleMapSlimDtoList.add(mockUserRoleMapSlimDto2);

		mockUserAndRoleDto = new UserAndRoleDTO("U05", "Homi", "Bhabha", "J", 2323232323L, "India", "IST",
				"www.linkedin.com/Ramanujan1234", "MCA", "MBA", "Indian scientist", "H1B", mockUserRoleMapSlimDtoList);


		mockUserDto1 = new UserDto("U05", "Homi", "Bhabha", "J", 2323232323L, "India", "IST",
				"www.linkedin.com/Ramanujan1234", "MCA", "MBA", "Indian scientist", "H1B");

		//given
		given(userService.createUserWithRole(ArgumentMatchers.any(UserAndRoleDTO.class))).willReturn(mockUserDto1);

		//when
		ResultActions response = mockMvc.perform(post("/users/roleStatus")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockUserAndRoleDto)));

		//then
		response.andExpect(status().isCreated())
				.andDo(print())
				.andExpect(jsonPath("$.userId",is(mockUserAndRoleDto.getUserId())))
				.andExpect(jsonPath("$.userFirstName",is(mockUserAndRoleDto.getUserFirstName())));

	}

	@Test
	@DisplayName("test to update user ")
	void testupdateUser() throws JsonProcessingException, Exception {

		String userId = "U01";
		UserDto updatedUserDTO = mockUserDto;
		updatedUserDTO.setUserTimeZone("EST");
		updatedUserDTO.setUserMiddleName("J");
		given(userService.updateUser(ArgumentMatchers.any(UserDto.class),
				ArgumentMatchers.any(String.class)))
				.willReturn(updatedUserDTO);

		ResultActions  response = mockMvc.perform(put("/users/{userId}",userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUserDTO)));

		response.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId",is(updatedUserDTO.getUserId())))
				.andExpect(jsonPath("$.userPhoneNumber",is(updatedUserDTO.getUserPhoneNumber())))
				.andExpect(jsonPath("$.userMiddleName",is(updatedUserDTO.getUserMiddleName())))
				.andExpect(jsonPath("$.userTimeZone",is(updatedUserDTO.getUserTimeZone())));

	}


	@DisplayName("test to update user - Not Found")
	@SneakyThrows
	@Test
	public void testupdateUserNoFound() {

		String userId = "U01";
		String message = "UserID: U01 doesnot exist ";
		UserDto updatedUserDTO = mockUserDto;
		updatedUserDTO.setUserTimeZone("EST");
		updatedUserDTO.setUserMiddleName("J");
		given(userService.updateUser(ArgumentMatchers.any(UserDto.class),
				ArgumentMatchers.any(String.class)))
				.willThrow(new ResourceNotFoundException(message));

		ResultActions  response = mockMvc.perform(put("/users/{userId}",userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUserDTO)));

		response.andExpect(status().isNotFound())
				.andDo(print())
				.andExpect(jsonPath("$.message").value(message));

	}

	@Test
	@DisplayName("test to delete an user ")
	void testdeleteUser() throws Exception {
		String userId = "U04";

		given(userService.deleteUser(userId)).willReturn(userId);

		ResultActions response = mockMvc.perform(delete("/users/{userId}",userId));

		response.andExpect(status().isOk())
				.andDo(print());
	}

	@Test
	@DisplayName("test to update user Role Status")
	void testupdateUserRoleStatus() throws JsonProcessingException, Exception {
		String userId = "U04";

		mockUserRoleMapSlimDto = new UserRoleMapSlimDTO("RO2","Active");

		UserRoleMapSlimDTO updatedUserRoleMapSLimDto = new UserRoleMapSlimDTO();
		updatedUserRoleMapSLimDto.setRoleId("RO2");
		updatedUserRoleMapSLimDto.setUserRoleStatus("InActive");

		given(userService.updateUserRoleStatus(ArgumentMatchers.any(UserRoleMapSlimDTO.class),
				ArgumentMatchers.any(String.class))).willReturn(userId);

		ResultActions response = mockMvc.perform(put("/users/roleStatus/{userId}",userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUserRoleMapSLimDto)));

		response.andExpect(status().isOk());
		//.andExpect(jsonPath("$.roleId").value("R02"));
	}

	@DisplayName("test to assign/update program/batch to Student")
	@SneakyThrows
	@Test
	public void testAssignUpdateUserRoleProgramBatchStatusForStudent() {
		String userId = "U07";
		String response = "Success for User with ID " + userId;

		when(userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDto, userId))
				.thenReturn(response);

		ResultActions resultActions = mockMvc.perform((put("/users/roleProgramBatchStatus/{userId}", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockUserRoleProgramBatchDto))));

		resultActions.andExpect(status().isOk()).andDo(print());
	}

	@DisplayName("test to assign/update program/batches to Staff")
	@SneakyThrows
	@Test
	public void testAssignUpdateUserRoleProgramBatchStatusForStaff() {
		String userId = "U02";
		String response = "Success for User with ID " + userId;

		when(userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDto2, userId))
				.thenReturn(response);

		ResultActions resultActions = mockMvc.perform((put("/users/roleProgramBatchStatus/{userId}", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockUserRoleProgramBatchDto2))));

		resultActions.andExpect(status().isOk()).andDo(print());
	}

}
