package com.numpyninja.lms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.TestWebSecurityConfig;
import com.numpyninja.lms.config.WithMockAdmin;
import com.numpyninja.lms.dto.*;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@Import(TestWebSecurityConfig.class)   // imports TestConfiguration
public class UserControllerTest {
	@Autowired
	private WebApplicationContext webApplicationContext;

	@MockBean
	private UserServices userService;

	@MockBean
	private UserMapper userMapper;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private UserDto mockUserDto, mockUserDto2;

	private UserRoleMap mockUserRoleMap;

	private UserAndRoleDTO mockUserAndRoleDto;
	
	private UserRoleIdDTO mockUserRoleIdDTO;

	private UserRoleMapSlimDTO mockUserRoleMapSlimDto;

	private UserRoleProgramBatchDto mockUserRoleProgramBatchDto, mockUserRoleProgramBatchDto2;

	private BatchSlimDto mockBatchSlimDto;

	private UserProgramBatchSlimDto mockUserProgramBatchSlimDto;

	private UserAllDto mockUserAllDto;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		setMockUserAndDto();
	}

	private void setMockUserAndDto() {
		// mockUser = new User("U01","Srinivasa", "Ramanujan"," ", 2323232323L, "India",
		// "IST", "www.linkedin.com/Ramanujan1234","MCA","MBA","Indian scientist","H1B",null, null);
		mockUserDto = new UserDto("U01", "Srinivasa", "Ramanujan", " ", 2323232323L, "India", "IST",
				"www.linkedin.com/Ramanujan1234", "MCA", "MBA", "Indian scientist", "H1B","srinivasa.ramanujan@gmail.com");

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

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches =
				List.of(new UserRoleProgramBatchSlimDto(2, "Active"));
		mockUserRoleProgramBatchDto = UserRoleProgramBatchDto.builder().roleId("R03").programId(2L)
				.userRoleProgramBatches(mockUserRoleProgramBatches).build();

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches2 =
				List.of(new UserRoleProgramBatchSlimDto(2, "Active"),
						new UserRoleProgramBatchSlimDto(5, "Active"));
		mockUserRoleProgramBatchDto2 = UserRoleProgramBatchDto.builder().roleId("R02").programId(2L)
				.userRoleProgramBatches(mockUserRoleProgramBatches2).build();

		mockUserDto2 = new UserDto("U07",  "Mary", "Poppins", "",
				9899245876L, "India", "IST", "www.linkedin.com/Mary123",
				"BCA", "MBA", "", "H4","mary.poppins@gmail.com");

		mockUserRoleMapSlimDto = new UserRoleMapSlimDTO("RO3","Active");

		mockBatchSlimDto = new BatchSlimDto(1, "SDET 01", "Active");

		mockUserProgramBatchSlimDto = new UserProgramBatchSlimDto(2L, "SDET", List.of(mockBatchSlimDto));

		mockUserAllDto = UserAllDto.builder()
				.userDto(mockUserDto2)
				.userRoleMaps(List.of(mockUserRoleMapSlimDto))
				.userProgramBatchSlimDtos(List.of(mockUserProgramBatchSlimDto))
				.build();

	}

	@Test
	@DisplayName("test to get all the users")
	void testGetAllUsers() throws Exception {

		UserDto mockUserDto2 = new UserDto("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST",
				"www.linkedin.com/Kalam1234", "MCA", "MBA", "Indian scientist", "H4","abdul.kalam@gmail.com");

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


	@DisplayName("test to get user Info for a given user ID")
	@SneakyThrows
	@Test
	void testGetUserInfoById() {
		String userId = "U07";

		given(userService.getUserInfoById(userId)).willReturn(mockUserAllDto);

		ResultActions resultActions = mockMvc.perform(get("/users/{id}", userId));

		resultActions.andExpect(status().isOk()).andDo(print());

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
				"www.linkedin.com/Ramanujan1234", "MCA", "MBA", "Indian scientist", "H1B","homi.bhabha@gmail.com");

		//given
		given(userService.createUserLoginWithRole(any(UserLoginRoleDTO.class))).willReturn(mockUserDto1);

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
		given(userService.updateUser(any(UserDto.class),
				any(String.class)))
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
		given(userService.updateUser(any(UserDto.class),
				any(String.class)))
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

		given(userService.updateUserRoleStatus(any(UserRoleMapSlimDTO.class),
				any(String.class))).willReturn(userId);

		ResultActions response = mockMvc.perform(put("/users/roleStatus/{userId}",userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUserRoleMapSLimDto)));

		response.andExpect(status().isOk());
		//.andExpect(jsonPath("$.roleId").value("R02"));
	}
	
	@Test
	@DisplayName("Test to update user Roles for single role")
	void  testupdateRoleId() throws JsonProcessingException, Exception { 
		String userId= "U01";
		List<String> roleList= new ArrayList();
		roleList.add("R03");
		mockUserRoleIdDTO = new UserRoleIdDTO(roleList);
		UserRoleIdDTO updatedUserRoleDto = new UserRoleIdDTO(); 
		List<String> updatedRoleList = new ArrayList();
		updatedRoleList.add("R02");
		updatedUserRoleDto.setUserRoleList(updatedRoleList);
		
		given(userService.updateRoleId(any(UserRoleIdDTO.class),
				any(String.class))).willReturn(userId);
		
		ResultActions response = mockMvc.perform(put("/users/roleId/{userId}",userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUserRoleDto)));
		
		response.andExpect(status().isOk());
		
	} 
	
	@Test
	@DisplayName("Test to update user Roles for two roles")
	void  testupdateRoleId1() throws JsonProcessingException, Exception { 
		String userId= "U08";
		List<String> roleList= new ArrayList();
		roleList.add("R01");
		roleList.add("R02");
		
		mockUserRoleIdDTO = new UserRoleIdDTO(roleList);
		UserRoleIdDTO updatedUserRoleDto = new UserRoleIdDTO(); 
		List<String> updatedRoleList = new ArrayList();
		updatedRoleList.add("R01");
		updatedRoleList.add("R02");
		
		updatedUserRoleDto.setUserRoleList(updatedRoleList);
		
		given(userService.updateRoleId(any(UserRoleIdDTO.class),
				any(String.class))).willReturn(userId);
		
		ResultActions response = mockMvc.perform(put("/users/roleId/{userId}",userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedUserRoleDto)));
		
		response.andExpect(status().isOk());
		
	} 


	@DisplayName("test to assign/update program/batch to Student")
	@SneakyThrows
	@Test
	public void testAssignUpdateUserRoleProgramBatchStatusForStudent() {
		String userId = "U07";
		String expectedResponse = "User " + userId + " has been successfully assigned to Program/Batch(es)";

		when(userService.assignUpdateUserRoleProgramBatchStatus(any(UserRoleProgramBatchDto.class), eq(userId)))
				.thenReturn(expectedResponse);

		ResultActions resultActions = mockMvc.perform((put("/users/roleProgramBatchStatus/{userId}", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockUserRoleProgramBatchDto))));

		MvcResult result = resultActions.andExpect(status().isOk()).andDo(print()).andReturn();
		String response = result.getResponse().getContentAsString();

		assertEquals(expectedResponse, response);
	}

	@DisplayName("test to assign/update program/batches to Staff")
	@SneakyThrows
	@Test
	public void testAssignUpdateUserRoleProgramBatchStatusForStaff() {
		String userId = "U09";
		String expectedResponse = "User " + userId + " has been successfully assigned to Program/Batch(es)";

		when(userService.assignUpdateUserRoleProgramBatchStatus(any(UserRoleProgramBatchDto.class), eq(userId)))
				.thenReturn(expectedResponse);

		ResultActions resultActions = mockMvc.perform((put("/users/roleProgramBatchStatus/{userId}", userId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(mockUserRoleProgramBatchDto2))));

		MvcResult result = resultActions.andExpect(status().isOk()).andDo(print()).andReturn();
		String response = result.getResponse().getContentAsString();

		assertEquals(expectedResponse, response);
	}


	@DisplayName("test to get user by program programId ")
	@SneakyThrows
	@Test
	void testGetUserByProgramBatches() {



		List<UserDto> userDtoList = new ArrayList<UserDto>();
		userDtoList.add(mockUserDto);

		Long programId = 1L;
		when(userService.getUsersByProgram(programId)).thenReturn(userDtoList);


		ResultActions response = mockMvc.perform(get("/users/programs/{programId}", programId));

		response.andExpect(status().isOk())
				.andExpect(jsonPath("$..userId")
						.value("U01"))
				.andExpect(jsonPath("$", hasSize(userDtoList.size())));

	}

	//changed name because there was conflict for get by programid and get by batchid
    @DisplayName("test to get user by program/batches batchid ")
    @SneakyThrows
	@Test
	void testGetUserByProgramBatchesBatchid() {

		List<UserDto> userDtoList = new ArrayList<UserDto>();
		userDtoList.add(mockUserDto);

		Integer batchid = 1;
		when(userService.getUserByProgramBatch(batchid)).thenReturn(userDtoList);


		ResultActions response = mockMvc.perform(get("/users/programBatch/{batchId}", batchid));

		response.andExpect(status().isOk())
		.andExpect(jsonPath("$..userId")
		.value("U01"))
	    .andExpect(jsonPath("$", hasSize(userDtoList.size())));

	}











}
