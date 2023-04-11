package com.numpyninja.lms.services;

import com.numpyninja.lms.dto.*;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.BatchMapper;
import com.numpyninja.lms.mappers.UserMapper;
import com.numpyninja.lms.mappers.UserPictureMapper;
import com.numpyninja.lms.mappers.UserSkillMapper;
import com.numpyninja.lms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
	private BatchMapper batchMapper;

	@Mock
	private UserSkillMapper userSkillMapper;

	@Mock
	private UserPictureMapper userPictureMapper;

	@Mock
	private ProgramRepository programRepository;

	@Mock
	private ProgBatchRepository progBatchRepository;

	@Mock
	private UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;

	@Mock
	private UserSkillRepository userSkillRepository;

	@Mock
	private UserPictureRepository userPictureRepository;

	private User mockUser, mockUser2, mockUser3;

	private UserDto mockUserDto, mockUserDto2, mockUserDto3;

	private UserRoleMap mockUserRoleMap, mockUserRoleMap2;

	private Role mockRole, mockRole2, mockRole3;

	private Program mockProgram;

	private Batch mockBatch, mockBatch2;

	private UserAndRoleDTO mockUserAndRoleDto;

	private UserRoleMapSlimDTO mockUserRoleMapSlimDto;

	private List<UserRoleMap> userRoleMapList;

	private List<UserRoleMapSlimDTO> userRoleMapsSlimList;

	private UserRoleProgramBatchDto mockUserRoleProgramBatchDtoWithBatch, mockUserRoleProgramBatchDtoWithBatches;

	private UserRoleProgramBatchMap mockUserRoleProgramBatchMap;

	private SkillMaster mockSkillMaster;

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

		String userRoleStatus = "Active";
		Timestamp Timestamp = new Timestamp(utilDate.getTime());

		Program program = new Program((long) 7, "Django", "new Prog", "nonActive", Timestamp, Timestamp);
		Batch batch = new Batch(1, "SDET 1", "SDET Batch 1", "Active", program, 5, Timestamp, Timestamp);
		Role userRole1 = new Role("R01","Staff","LMS_Staff",Timestamp,Timestamp);
		Role userRole2= new Role("R02","User","LMS_User",Timestamp,Timestamp);
		mockRole = new Role("R01","Staff","LMS_Staff",Timestamp,Timestamp);

		mockUserRoleMap = new UserRoleMap(1L,mockUser,userRole2,userRoleStatus,Timestamp,Timestamp);

		mockUserRoleMap2 = new UserRoleMap(2L,mockUser,userRole1,userRoleStatus,Timestamp,Timestamp);

		userRoleMapsSlimList = new ArrayList<>();

		mockUserRoleMapSlimDto = new UserRoleMapSlimDTO("R01", "Active");
		userRoleMapsSlimList.add(mockUserRoleMapSlimDto);

		mockUserAndRoleDto = new UserAndRoleDTO("U02", "Abdul", "Kalam", " ", 2222222222L, "India", "IST", "www.linkedin.com/Kalam1234",
				"MCA", "MBA", "Indian scientist", "H4", userRoleMapsSlimList);

		mockUser2 = new User("U07", "Mary", "Poppins", "",
				9899245876L, "India", "IST", "www.linkedin.com/Mary123",
				"BCA", "MBA", "", "H4", Timestamp.valueOf(LocalDateTime.now()),
				Timestamp.valueOf(LocalDateTime.now()));

		mockUserDto2 = new UserDto("U07", "Mary", "Poppins", "",
				9899245876L, "India", "IST", "www.linkedin.com/Mary123",
				"BCA", "MBA", "", "H4");

		mockRole2 = new Role("R03","Student","LMS_User",Timestamp.valueOf(LocalDateTime.now()),
				Timestamp.valueOf(LocalDateTime.now()));

		mockUser3 = new User("U02", "Steve", "Jobs", "",
				9899245877L, "India", "IST", "www.linkedin.com/Steve123",
				"BE", "MBA", "", "H4", Timestamp.valueOf(LocalDateTime.now()),
				Timestamp.valueOf(LocalDateTime.now()));

		mockUserDto3 = new UserDto("U02", "Steve", "Jobs", "",
				9899245877L, "India", "IST", "www.linkedin.com/Steve123",
				"BE", "MBA", "", "H4");

		mockRole3 = new Role("R02","Staff","LMS_Staff",Timestamp.valueOf(LocalDateTime.now()),
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

		mockBatch2 = new Batch(2, "SDET 2", "", "Active", mockProgram,
				7, Timestamp.valueOf(LocalDateTime.now()), Timestamp.valueOf(LocalDateTime.now()));

		mockUserRoleProgramBatchMap = new UserRoleProgramBatchMap(1L, mockUser2, mockRole2,
				mockProgram, mockBatch, "Active",  Timestamp.valueOf(LocalDateTime.now()),
				Timestamp.valueOf(LocalDateTime.now()));

		mockSkillMaster = new SkillMaster(1L, "Java", Timestamp.valueOf(LocalDateTime.now()),
				Timestamp.valueOf(LocalDateTime.now()));

		return mockUserDto;
	}

	@DisplayName("test for creating user with Role info")
	@Test
		//@Order(2)
	void createUserWithRole() throws InvalidDataException, DuplicateResourceFoundException {

		String roleId = "R01";

		userRoleMapList = new ArrayList<>();
		userRoleMapList.add(mockUserRoleMap);
		userRoleMapList.add(mockUserRoleMap2);

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

	@DisplayName("test for getting User Info for a given userId - When User not found")
	@Test
	void testGetUserInfoByIdWhenUserNotFound() {
		String userId = "U99";
		String message = String.format("User not found with Id : %s ", userId);

		when(userRepo.findById(userId)).thenReturn(Optional.empty());

		Exception ex = assertThrows(ResourceNotFoundException.class,
				() -> userService.getUserInfoById(userId));

		assertEquals(message, ex.getMessage());
	}

	@DisplayName("test for getting User Info for a given userId with role Student")
	@Test
	void testGetUserInfoByIdForStudent() {
		String userId = "U07";
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		List<UserRoleMap> mockUserRoleMaps = List.of(new UserRoleMap(1L, mockUser2, mockRole2,
				"Active", timestamp, timestamp));
		List<UserRoleMapSlimDTO> mockUserRoleMapSlimDtos = List.of(new UserRoleMapSlimDTO(mockRole2.getRoleId(),
				"Active"));

		List<UserRoleProgramBatchMap> mockUserRoleProgramBatchMaps = List.of(new UserRoleProgramBatchMap(
				1L, mockUser2, mockRole2, mockProgram, mockBatch, "Active",
				timestamp, timestamp));
		List<BatchSlimDto> mockBatchslimDtos = List.of(new BatchSlimDto(mockBatch.getBatchId(), mockBatch.getBatchName(),
				"Active"));

		List<UserSkill> mockUserSkills = List.of(new UserSkill("US01", mockUser2, mockSkillMaster, 36,
				timestamp, timestamp));
		List<UserSkillSlimDto> mockUserSkillSlimDtos = List.of(new UserSkillSlimDto(mockSkillMaster.getSkillId(),
				mockSkillMaster.getSkillName(), 36));

		List<UserPictureEntity> mockUserPictureEntityList = List.of(new UserPictureEntity(1L, "ProfilePic",
				mockUser2, "C:\\Images"));
		List<UserPictureSlimDto> mockUserPictureSlimDtos = List.of(new UserPictureSlimDto(1L, "ProfilePic",
				"C:\\Images"));

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(userRoleMapRepository.findUserRoleMapsByUserUserId(userId)).thenReturn(mockUserRoleMaps);
		when(userMapper.userDto(mockUser2)).thenReturn(mockUserDto2);
		when(userMapper.toUserRoleMapSlimDtos(mockUserRoleMaps)).thenReturn(mockUserRoleMapSlimDtos);
		when(userRoleProgramBatchMapRepository.findByUser_UserId(userId)).thenReturn(mockUserRoleProgramBatchMaps);
		when(batchMapper.toBatchSlimDtoList(anyList())).thenReturn(mockBatchslimDtos);
		when(userSkillRepository.findByUserId(userId)).thenReturn(mockUserSkills);
		when(userSkillMapper.toUserSkillSlimDtoList(mockUserSkills)).thenReturn(mockUserSkillSlimDtos);
		when(userPictureRepository.findByUser_UserId(userId)).thenReturn(mockUserPictureEntityList);
		when(userPictureMapper.toUserPictureSlimDtoList(mockUserPictureEntityList)).thenReturn(mockUserPictureSlimDtos);

		UserAllDto responseUserAllDto = userService.getUserInfoById(userId);

		assertThat(responseUserAllDto).isNotNull();
		assertEquals(2L, responseUserAllDto.getUserProgramBatchSlimDtos().get(0).getProgramId());
		assertEquals("Java",  responseUserAllDto.getUserSkillSlimDtos().get(0).getSkillName());
		assertEquals("ProfilePic",  responseUserAllDto.getUserPictureSlimDtos().get(0).getUserFileType());
	}

	@DisplayName("test for getting User Info for a given userId with role Staff")
	@Test
	void testGetUserInfoByIdForStaff() {
		String userId = "U02";
		Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());

		List<UserRoleMap> mockUserRoleMaps = List.of(new UserRoleMap(2L, mockUser3, mockRole3,
				"Active", timestamp, timestamp));
		List<UserRoleMapSlimDTO> mockUserRoleMapSlimDtos = List.of(new UserRoleMapSlimDTO(mockRole3.getRoleId(),
				"Active"));

		List<UserRoleProgramBatchMap> mockUserRoleProgramBatchMaps = List.of(
				new UserRoleProgramBatchMap(2L, mockUser3, mockRole3, mockProgram, mockBatch,
						"Active",timestamp, timestamp),
				new UserRoleProgramBatchMap(3L, mockUser3, mockRole3, mockProgram, mockBatch2,
						"Active",timestamp, timestamp));
		List<BatchSlimDto> mockBatchslimDtos = List.of(
				new BatchSlimDto(mockBatch.getBatchId(), mockBatch.getBatchName(),"Active"),
				new BatchSlimDto(mockBatch2.getBatchId(), mockBatch2.getBatchName(),"Active"));

		List<UserPictureEntity> mockUserPictureEntityList = List.of(new UserPictureEntity(2L, "Resume",
				mockUser2, "C:\\Documents"));
		List<UserPictureSlimDto> mockUserPictureSlimDtos = List.of(new UserPictureSlimDto(2L, "Resume",
				"C:\\Documents"));

		when(userRepo.findById(userId)).thenReturn(Optional.of(mockUser2));
		when(userRoleMapRepository.findUserRoleMapsByUserUserId(userId)).thenReturn(mockUserRoleMaps);
		when(userMapper.userDto(mockUser2)).thenReturn(mockUserDto2);
		when(userMapper.toUserRoleMapSlimDtos(mockUserRoleMaps)).thenReturn(mockUserRoleMapSlimDtos);
		when(userRoleProgramBatchMapRepository.findByUser_UserId(userId)).thenReturn(mockUserRoleProgramBatchMaps);
		when(batchMapper.toBatchSlimDtoList(anyList())).thenReturn(mockBatchslimDtos);
		when(userSkillRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
		when(userPictureRepository.findByUser_UserId(userId)).thenReturn(mockUserPictureEntityList);
		when(userPictureMapper.toUserPictureSlimDtoList(mockUserPictureEntityList)).thenReturn(mockUserPictureSlimDtos);

		UserAllDto responseUserAllDto = userService.getUserInfoById(userId);

		assertThat(responseUserAllDto).isNotNull();
		assertEquals(2L, responseUserAllDto.getUserProgramBatchSlimDtos().get(0).getProgramId());
		assertEquals("Resume",  responseUserAllDto.getUserPictureSlimDtos().get(0).getUserFileType());

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
