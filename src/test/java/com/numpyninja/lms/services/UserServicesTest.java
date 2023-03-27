package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.*;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserMapper;
import com.numpyninja.lms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServicesTest {

	@Mock
	private UserRepository userRepo;

	@InjectMocks
	private UserServices userService;

	@Mock
	UserRoleMapRepository userRoleMapRepository;

	@Mock
	RoleRepository roleRepository;

	@Mock
	private UserMapper userMapper;

	@Mock
	private ProgramRepository programRepository;

	@Mock
	private ProgBatchRepository progBatchRepository;

	@Mock
	private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;

	private User mockUser, mockUser2;

	private UserDto mockUserDto;

	private UserMapper userMapper1 ;

	private UserRoleMap mockUserRoleMap;
	private UserRoleMap mockUserRoleMap1;

	private Role mockRole, mockRole2;

	private Program mockProgram;

	private Batch mockBatch;

	private UserAndRoleDTO mockUserAndRoleDto;

	private UserRoleMapSlimDTO mockUserRoleMapSlimDto;


	private List<UserRoleMap> userRoleMapList;

	private List<UserRoleMapSlimDTO> userRoleMapsSlimList;

	private UserRoleProgramBatchDto mockUserRoleProgramBatchDtoWithBatch, mockUserRoleProgramBatchDtoWithBatches;


	private UserRoleProgramBatchMap mockUserRoleProgramBatchMap;

	@BeforeEach
	void setUp() {
		mockUserDto = setupUserAndUserDTO();

	}

	public UserDto setupUserAndUserDTO() {


		Date utilDate = new Date();
		mockUser = new User("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST", "www.linkedin.com/Kalam1234",
				"MCA", "MBA", "Indian scientist", "H4", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));

		mockUserDto = new UserDto("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST", "www.linkedin.com/Kalam1234",
				"MCA", "MBA", "Indian scientist", "H4");

		Long userRoleId = 1L;
		String userRoleStatus = "Active";
		Timestamp Timestamp = new Timestamp(utilDate.getTime());

		Program program = new Program((long) 7, "Django", "new Prog", "nonActive", Timestamp, Timestamp);
		Batch batch = new Batch(1, "SDET 1", "SDET Batch 1", "Active", program, 5, Timestamp, Timestamp);
		Role userRole1 = new Role("R01","Staff","LMS_Staff",Timestamp,Timestamp);
		Role userRole2= new Role("R02","User","LMS_User",Timestamp,Timestamp);
		mockRole = new Role("R01","Staff","LMS_Staff",Timestamp,Timestamp);

		Set<Batch> batchSet = new HashSet<Batch>();
		batchSet.add(batch);

		mockUserRoleMap = new UserRoleMap(userRoleId,mockUser,userRole2,batchSet,userRoleStatus,Timestamp,Timestamp);


		mockUserRoleMap1 = new UserRoleMap(userRoleId,mockUser,userRole1,batchSet,userRoleStatus,Timestamp,Timestamp);

		userRoleMapsSlimList = new ArrayList<>();

		mockUserRoleMapSlimDto = new UserRoleMapSlimDTO("R01", "Active");
		userRoleMapsSlimList.add(mockUserRoleMapSlimDto);

		mockUserAndRoleDto = new UserAndRoleDTO("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST", "www.linkedin.com/Kalam1234",
				"MCA", "MBA", "Indian scientist", "H4", userRoleMapsSlimList);

		mockUser2 = new User("U07", "Mary", "Poppins", "",
				9899245876L, "India", "IST", "www.linkedin.com/Mary123",
				"BCA", "MBA", "", "H4", Timestamp.valueOf(LocalDateTime.now()),
				Timestamp.valueOf(LocalDateTime.now()));

		mockRole2 = new Role("R03","Student","LMS_User",Timestamp.valueOf(LocalDateTime.now()),
				Timestamp.valueOf(LocalDateTime.now()));

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches =
				List.of(new UserRoleProgramBatchSlimDto(2, "Active"));
		mockUserRoleProgramBatchDtoWithBatch = UserRoleProgramBatchDto.builder().roleId("R03").programId(2L)
				.userRoleProgramBatches(mockUserRoleProgramBatches).build();

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches2 =
				List.of(new UserRoleProgramBatchSlimDto(2, "Active"),
						new UserRoleProgramBatchSlimDto(5, "Active"));
		mockUserRoleProgramBatchDtoWithBatches = UserRoleProgramBatchDto.builder().roleId("R02").programId(2L)
				.userRoleProgramBatches(mockUserRoleProgramBatches2).build();

		mockProgram = new Program(2L, "SDET", "", "Active",
				Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));

		mockBatch = new Batch(1, "SDET 1", "", "Active", mockProgram,
				5, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));

		mockUserRoleProgramBatchMap = new UserRoleProgramBatchMap(1L, mockUser2, mockRole2,
				mockProgram, mockBatch, "Active",  Timestamp.valueOf(LocalDateTime.now()),
				Timestamp.valueOf(LocalDateTime.now()));

		return mockUserDto;
	}

	@DisplayName("test for createUser method")
	@Test
		//@Order(2)
	void createUserTest() throws InvalidDataException, DuplicateResourceFoundException {

		//given(userMapper.user(org.mockito.ArgumentMatchers.any())).willReturn(mockUser);

		//given(userRepo.findByUserPhoneNumber(mockUserDto.getUserPhoneNumber()))
		//	.willReturn(Optional.empty());
		given(userMapper.user(mockUserDto)).willReturn(mockUser);
		given(userRepo.save(mockUser)).willReturn(mockUser);
		given(userMapper.userDto(mockUser)).willReturn(mockUserDto)	;

		//when
		UserDto userDto = userService.createUser(mockUserDto);

		//then
		assertThat(userDto).isNotNull();

	}


	@DisplayName("test for creating user with Role info")
	@Test
		//@Order(2)
	void createUserWithRole() throws InvalidDataException, DuplicateResourceFoundException {

		String roleId = "R01";

		userRoleMapList = new ArrayList<>();
		userRoleMapList.add(mockUserRoleMap);
		userRoleMapList.add(mockUserRoleMap1);

		given(userMapper.toUser(mockUserAndRoleDto)).willReturn(mockUser);
		given(userRepo.save(mockUser)).willReturn(mockUser);
		given(roleRepository.getById(roleId)).willReturn(mockRole);
		given(userMapper.userRoleMapList(mockUserAndRoleDto.getUserRoleMaps())).willReturn(userRoleMapList);
		given(userRoleMapRepository.save(userRoleMapList.get(0))).willReturn(mockUserRoleMap);

		given(userMapper.userDto(mockUser)).willReturn(mockUserDto);

		//when
		UserDto userDto  = userService.createUserWithRole(mockUserAndRoleDto);
		//then
		assertThat(userDto).isNotNull();

	}


	@DisplayName("test for creating a new user with duplicate phone number - throws exception")
		//@Test
		//@Order(3)
	void testCreateUserWithDuplicatePhoneNumber()  {

		long userPhoneNum = 2222222222L;
		//mockUserDto.getUserPhoneNumber()
		given(userRepo.findByUserPhoneNumber(userPhoneNum))
				.willReturn(Optional.empty());

		//System.out.println("mockUserDto...... " +mockUserDto.getUserLastName() + mockUserDto.getUserPhoneNumber());
		// when
		assertThrows(DuplicateResourceFoundException.class, ()->userService.createUser(mockUserDto));

		Mockito.verify(userMapper, never()).user(any(UserDto.class));

		verify(userRepo, never()).save(any(User.class));
		verify(userMapper,never()).userDto(any(User.class));
	}


	@DisplayName("test for getAllUsers method")
	@Test
		//@Order(1)
	void getAllUsersTest() {

		User mockuser2 = mockUser;
		mockuser2.setUserId("U03");
		mockuser2.setUserFirstName("Homi");
		mockuser2.setUserLastName("Baba");
		mockuser2.setUserPhoneNumber(1122112211L);


		List<User>  userList = new ArrayList<>();
		userList.add(mockUser);
		userList.add(mockuser2);

		UserDto mockUserMapper = userMapper.userDto(mockUser);
		UserDto mockUserMapper1 = userMapper.userDto(mockuser2);
		List<UserDto>  userMapperDtoList = new ArrayList<>();
		userMapperDtoList.add(mockUserMapper);
		userMapperDtoList.add(mockUserMapper1);

		given(userRepo.findAll()).willReturn(userList);

		given(userMapper.userDtos(userList)).willReturn(userMapperDtoList);

		//when
		List<UserDto> userDto = userService.getAllUsers();

		assertThat(userDto).isNotNull();

	}

	@DisplayName("test for deleting an user by id")
	@Test
		//@Order(4)
	void testDeleteUser() {

		//given
		given(userRepo.existsById(mockUser.getUserId())).willReturn(true);
		willDoNothing().given(userRepo).deleteById(mockUser.getUserId());

		//when
		userService.deleteUser(mockUser.getUserId());

		//then
		verify(userRepo, times(1)).deleteById(mockUser.getUserId());
		verify(userRepo).existsById(mockUser.getUserId());
		verify(userRepo).deleteById(mockUser.getUserId());

	}

	@DisplayName("test for getting User Info for a given userId")
	@Test
	void getUserInfoByIdTest() {
		userRoleMapList = new ArrayList<>();
		userRoleMapList.add(mockUserRoleMap);
		userRoleMapList.add(mockUserRoleMap);

		given(userRepo.findById(mockUser.getUserId())).willReturn(Optional.of(mockUser));
		given(userRoleMapRepository.findUserRoleMapsByUserUserId(mockUser.getUserId())).willReturn(userRoleMapList);

		//when
		List<UserRoleMap> userRoleMapListOut = userService.getUserInfoById(mockUser.getUserId());

		//then
		assertThat(userRoleMapListOut).isNotNull();
		assertThat(userRoleMapListOut.size()).isGreaterThan(0);

	}

	@DisplayName("test for getting list of all Users - with all their info - user,role,batch")
	@Test
	void getAllUsersWithRolesTest() {

		Date utilDate = new Date();
		Timestamp Timestamp = new Timestamp(utilDate.getTime());

		Long userRoleId = 1L;
		String userRoleStatus = "Active";
		Role userRole3= new Role("R02","User","LMS_User",Timestamp,Timestamp);
		Set<Batch> batchSet1 = new HashSet<Batch>();


		Program program = new Program((long) 7, "Python", "new Prog", "Active", Timestamp, Timestamp);
		Batch batch2 = new Batch(1, "Python 1", "Python Batch 1", "Active", program, 5, Timestamp, Timestamp);
		batchSet1.add(batch2);


		User mockUser2 = new User("U03", "Mary", "Poppins", " ", 9562867512L, "USA", "EST", "www.linkedin.com/Poppins1234",
				"MCA", "MBA", "Actor", "H4", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));

		User mockUser3 = new User("U04", "Stephen", "Hawking", " ", 1111111111L, "UK", "CST", "www.linkedin.com/Hawking1234",
				"MCA", "MBA", "Physicist", "H4", new Timestamp(utilDate.getTime()),
				new Timestamp(utilDate.getTime()));

		UserRoleMap mockUserRoleMap2 = new UserRoleMap(userRoleId,mockUser2,userRole3,batchSet1,userRoleStatus,Timestamp,Timestamp);
		UserRoleMap mockUserRoleMap3 = new UserRoleMap(userRoleId,mockUser3,userRole3,batchSet1,userRoleStatus,Timestamp,Timestamp);

		userRoleMapList = new ArrayList<>();
		userRoleMapList.add(mockUserRoleMap);
		userRoleMapList.add(mockUserRoleMap);
		userRoleMapList.add(mockUserRoleMap2);
		userRoleMapList.add(mockUserRoleMap3);

		//given
		given(userRoleMapRepository.findAll()).willReturn(userRoleMapList);


		//when
		List<UserRoleMap> allUsersRoleMapList = userService.getAllUsersWithRoles();

		//then
		assertThat(allUsersRoleMapList).isNotNull();
		assertThat(allUsersRoleMapList.size()).isGreaterThan(0);
		assertThat(allUsersRoleMapList.size()).isEqualTo(4);

	}

	@DisplayName("test for updating an User")
	@Test
	void testUpdateUser() throws ResourceNotFoundException, InvalidDataException {
		//given		
		given(userRepo.findById(mockUser.getUserId())).willReturn(Optional.of(mockUser));
		mockUserDto.setUserMiddleName("APJ");
		given(userMapper.user(mockUserDto)).willReturn(mockUser);
		given(userRepo.save(mockUser)).willReturn(mockUser);
		given(userMapper.userDto(mockUser)).willReturn(mockUserDto);

		//when
		UserDto userDto = userService.updateUser(mockUserDto, mockUser.getUserId());

		//then
		assertThat(userDto).isNotNull();
		assertThat(userDto.getUserMiddleName()).isEqualTo("APJ");

	}

	/** JUnit test cases for mapping program/batch(es) to Student/Staff : START **/
	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Validate ProgramId")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramId() {
		String userId = "U07";
		String message = "Program Id must be greater than or equal to 1 \n ";

		mockUserRoleProgramBatchDtoWithBatch.setProgramId(-11L);

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Validate BatchId")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateBatchId() {
		String userId = "U07";
		String message = "Batch Id must be greater than or equal to 1 \n ";

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches =
				List.of(new UserRoleProgramBatchSlimDto(-2, "Active"));
		mockUserRoleProgramBatchDtoWithBatch.setUserRoleProgramBatches(mockUserRoleProgramBatches);

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - User-Role-Program-Batch Status is not present")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_UserRoleProgramBatchStatusNotPresent() {
		String userId = "U07";
		String message = "User-Role-Program-Batch Status is Mandatory \n ";

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches =
				List.of(new UserRoleProgramBatchSlimDto(2, null));
		mockUserRoleProgramBatchDtoWithBatch.setUserRoleProgramBatches(mockUserRoleProgramBatches);

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Validate User-Role-Program-Batch Status")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateUserRoleProgramBatchStatus() {
		String userId = "U07";
		String message = "User-Role-Program-Batch Status can be Active or Inactive \n ";

		List<UserRoleProgramBatchSlimDto> mockUserRoleProgramBatches =
				List.of(new UserRoleProgramBatchSlimDto(2, "Hello"));
		mockUserRoleProgramBatchDtoWithBatch.setUserRoleProgramBatches(mockUserRoleProgramBatches);

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When User is not present")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_WhenUserNotFound() {
		String userId = "U11";
		String message = String.format("User not found with Id : %s ", userId);

		when(userRepo.findById(userId)).thenReturn(Optional.empty());

		Exception ex = assertThrows(ResourceNotFoundException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When Role is not present")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_WhenRoleNotFound() {
		String userId = "U07";
		String roleId = "R06";
		String message = String.format("Role not found with Id : %s ", roleId);

		mockUser.setUserId("U07");
		mockUserRoleProgramBatchDtoWithBatch.setRoleId(roleId);

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.empty());

		Exception ex = assertThrows(ResourceNotFoundException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When User-Role mapping is not present")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_WhenUserRoleMappingNotFound() {
		String userId = "U02";
		String message = String.format("User not found with Role : %s ", mockUserRoleProgramBatchDtoWithBatch.getRoleId());

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(false);

		Exception ex = assertThrows(ResourceNotFoundException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When Program is not present")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_WhenProgramNotFound() {
		String userId = "U07";
		mockUserRoleProgramBatchDtoWithBatch.setProgramId(11L);

		String message = "Program " + mockUserRoleProgramBatchDtoWithBatch.getProgramId()
				+ " not found with Program Status : Active ";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.empty());

		Exception ex = assertThrows(ResourceNotFoundException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Validate single program/batch for Student - " +
			"Scenario: multiple batches are present")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStudent1() {
		String userId = "U07";
		mockUserRoleProgramBatchDtoWithBatches.setRoleId("R03");

		String message = "User with Role " + mockUserRoleProgramBatchDtoWithBatches.getRoleId() +
				" can be assigned to single program/batch";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatches, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Validate Program-Batch mapping for Student - " +
			"Scenario: given batch is inactive or not mapped to given program")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStudent2() {
		String userId = "U07";
		String message = "Batch " + mockUserRoleProgramBatchDtoWithBatch.getUserRoleProgramBatches().get(0).getBatchId()
				+ " not found with Status as Active for Program " + mockUserRoleProgramBatchDtoWithBatch.getProgramId()
				+ "  \n ";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.empty());

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When Existing Program-Batch is active for Student" +
			"and received Program-Batch mapping for same Program with different Batch & batchStatus as Active")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStudent3() {
		String userId = "U07";
		String message = "Please deactivate User from existing program/batch and then activate for another program/batch";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.of(mockBatch));
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
						(anyString(), anyString(), anyString()))
				.thenReturn(Optional.of(mockUserRoleProgramBatchMap));

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When Existing Program-Batch is active for Student" +
			"and received Program-Batch mapping for same Program with different Batch & batchStatus as Inactive")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStudent4() {
		String userId = "U07";
		mockUserRoleProgramBatchDtoWithBatch.getUserRoleProgramBatches().get(0).setUserRoleProgramBatchStatus("Inactive");

		String message = "Please deactivate User from existing program/batch and then activate for another program/batch";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.of(mockBatch));
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
						(anyString(), anyString(), anyString()))
				.thenReturn(Optional.of(mockUserRoleProgramBatchMap));

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When Existing Program-Batch is active for Student" +
			"and received Program-Batch mapping for different Program & batchStatus as Active/Inactive")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStudent5() {
		String userId = "U07";
		mockUserRoleProgramBatchDtoWithBatch.setProgramId(1L);

		String message = "Please deactivate User from existing program/batch and then activate for another program/batch";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.of(mockBatch));
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
						(anyString(), anyString(), anyString()))
				.thenReturn(Optional.of(mockUserRoleProgramBatchMap));

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Received Program-Batch mapping for Student")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStudent6() {
		String userId = "U07";
		String message = "User " + userId + " has been successfully assigned to Program/Batch(es)";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.of(mockBatch));
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
						(anyString(), anyString(), anyString()))
				.thenReturn(Optional.empty());
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId(anyString(), anyString(),
						anyLong(), anyInt())).thenReturn(Optional.empty());
		when(userMapper.toUserRoleProgramBatchMap(any(UserRoleProgramBatchSlimDto.class)))
				.thenReturn(mockUserRoleProgramBatchMap);
		when(userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap)).thenReturn(mockUserRoleProgramBatchMap);

		String response = userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId);

		assertEquals(message, response);
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When Existing Program-Batch is active and " +
			"received same Program-Batch mapping for Student")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStudent7() {
		String userId = "U07";
		String message = "User " + userId + " has been successfully assigned to Program/Batch(es)";
		mockUserRoleProgramBatchDtoWithBatch.getUserRoleProgramBatches().get(0).setBatchId(1);
		mockUserRoleProgramBatchDtoWithBatch.getUserRoleProgramBatches().get(0).setUserRoleProgramBatchStatus("Inactive");


		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.of(mockBatch));
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
						(anyString(), anyString(), anyString()))
				.thenReturn(Optional.of(mockUserRoleProgramBatchMap));
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId(anyString(), anyString(),
						anyLong(), anyInt())).thenReturn(Optional.of(mockUserRoleProgramBatchMap));
		lenient().when(userMapper.toUserRoleProgramBatchMap(any(UserRoleProgramBatchSlimDto.class)))
				.thenReturn(mockUserRoleProgramBatchMap);
		when(userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap)).thenReturn(mockUserRoleProgramBatchMap);

		String response = userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId);

		assertEquals(message, response);
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Validate single Program-Batch mapping for Staff - " +
			"Scenario: given batch is inactive or not mapped to given program")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStaff1() {
		String userId = "U09";
		mockUserRoleProgramBatchDtoWithBatch.setRoleId("R02");

		String message = "Batch " + mockUserRoleProgramBatchDtoWithBatch.getUserRoleProgramBatches().get(0).getBatchId()
				+ " not found with Status as Active for Program " + mockUserRoleProgramBatchDtoWithBatch.getProgramId()
				+ "  \n ";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.empty());

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Received single Program-Batch mapping for Staff")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStaff2() {
		String userId = "U09";
		String message = "User " + userId + " has been successfully assigned to Program/Batch(es)";
		mockUserRoleProgramBatchDtoWithBatch.setRoleId("R02");

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.of(mockBatch));
		lenient().when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
						(anyString(), anyString(), anyString()))
				.thenReturn(Optional.empty());
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId(anyString(), anyString(),
						anyLong(), anyInt())).thenReturn(Optional.empty());
		when(userMapper.toUserRoleProgramBatchMap(any(UserRoleProgramBatchSlimDto.class)))
				.thenReturn(mockUserRoleProgramBatchMap);
		when(userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap)).thenReturn(mockUserRoleProgramBatchMap);

		String response = userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId);

		assertEquals(message, response);
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - When Existing Program-Batch is active and " +
			"received same Program-Batch mapping for Staff")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStaff3() {
		String userId = "U09";
		String message = "User " + userId + " has been successfully assigned to Program/Batch(es)";
		mockUserRoleProgramBatchDtoWithBatch.setRoleId("R02");
		mockUserRoleProgramBatchDtoWithBatch.getUserRoleProgramBatches().get(0).setBatchId(1);
		mockUserRoleProgramBatchDtoWithBatch.getUserRoleProgramBatches().get(0).setUserRoleProgramBatchStatus("Inactive");


		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.of(mockBatch));
		lenient().when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
						(anyString(), anyString(), anyString()))
				.thenReturn(Optional.of(mockUserRoleProgramBatchMap));
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId(anyString(), anyString(),
						anyLong(), anyInt())).thenReturn(Optional.of(mockUserRoleProgramBatchMap));
		lenient().when(userMapper.toUserRoleProgramBatchMap(any(UserRoleProgramBatchSlimDto.class)))
				.thenReturn(mockUserRoleProgramBatchMap);
		when(userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap)).thenReturn(mockUserRoleProgramBatchMap);

		String response = userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatch, userId);

		assertEquals(message, response);
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Validate multiple Program-Batches mapping for Staff - " +
			"Scenario: given all batches are inactive or not mapped to given program")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStaff4() {
		String userId = "U09";

		String message = "Batch " + mockUserRoleProgramBatchDtoWithBatches.getUserRoleProgramBatches().get(0).getBatchId()
				+ " not found with Status as Active for Program " + mockUserRoleProgramBatchDtoWithBatches.getProgramId()
				+ "  \n "
				+ "Batch " + mockUserRoleProgramBatchDtoWithBatches.getUserRoleProgramBatches().get(1).getBatchId()
				+ " not found with Status as Active for Program " + mockUserRoleProgramBatchDtoWithBatches.getProgramId()
				+ "  \n ";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.empty());

		Exception ex = assertThrows(InvalidDataException.class,
				() -> userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatches, userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Validate multiple Program-Batches mapping for Staff - " +
			"Scenario: given some batches are inactive or not mapped to given program")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStaff5() {
		String userId = "U09";

		String message = "User " + userId + " has failed for - Batch " +
				mockUserRoleProgramBatchDtoWithBatches.getUserRoleProgramBatches().get(1).getBatchId()
				+ " not found with Status as Active for Program " + mockUserRoleProgramBatchDtoWithBatches.getProgramId()
				+ "  \n ";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(eq(2), eq(2L), eq("Active"))).thenReturn(Optional.of(mockBatch));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(eq(5), eq(2L), eq("Active"))).thenReturn(Optional.empty());
		lenient().when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId(eq(userId), eq("R02"),
						eq(2L), eq(2))).thenReturn(Optional.of(mockUserRoleProgramBatchMap));
		when(userMapper.toUserRoleProgramBatchMap(any(UserRoleProgramBatchSlimDto.class)))
				.thenReturn(mockUserRoleProgramBatchMap);
		when(userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap)).thenReturn(mockUserRoleProgramBatchMap);

		String response = userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatches, userId);

		assertEquals(message, response);
	}

	@DisplayName("test - assignUpdateUserRoleProgramBatchStatus - Received multiple Program-Batches mapping for Staff")
	@Test
	void testAssignUpdateUserRoleProgramBatchStatus_ValidateProgramBatchForStaff6() {
		String userId = "U09";
		String message = "User " + userId + " has been successfully assigned to Program/Batch(es)";

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(roleRepository.findById(anyString())).thenReturn(Optional.of(mockRole2));
		when(userRoleMapRepository.existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase
				(anyString(), anyString(), anyString())).thenReturn(true);
		when(programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(anyLong(), anyString()))
				.thenReturn(Optional.of(mockProgram));
		when(progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
				(anyInt(), anyLong(), anyString())).thenReturn(Optional.of(mockBatch));
		lenient().when(userRoleProgramBatchMapRepository
						.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
								(anyString(), anyString(), anyString()))
				.thenReturn(Optional.empty());
		when(userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId(anyString(), anyString(),
						anyLong(), anyInt())).thenReturn(Optional.empty());
		when(userMapper.toUserRoleProgramBatchMap(any(UserRoleProgramBatchSlimDto.class)))
				.thenReturn(mockUserRoleProgramBatchMap);
		when(userRoleProgramBatchMapRepository.save(mockUserRoleProgramBatchMap)).thenReturn(mockUserRoleProgramBatchMap);

		String response = userService.assignUpdateUserRoleProgramBatchStatus(mockUserRoleProgramBatchDtoWithBatches, userId);

		assertEquals(message, response);
	}
	/** JUnit test cases for mapping program/batch(es) to Student/Staff : END **/
}
