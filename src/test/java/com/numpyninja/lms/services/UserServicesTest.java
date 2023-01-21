package com.numpyninja.lms.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.UserAndRoleDTO;
import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.dto.UserRoleMapSlimDTO;
import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.Program;
import com.numpyninja.lms.entity.Role;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleMap;
import com.numpyninja.lms.exception.DuplicateResourceFound;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserMapper;
import com.numpyninja.lms.repository.RoleRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;

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
	
	private User mockUser;
	
	private UserDto mockUserDto;
	
	private UserMapper userMapper1 ;
	
	private UserRoleMap mockUserRoleMap;
	private UserRoleMap mockUserRoleMap1;
	
	private Role mockRole;
	
	private Batch mockBatch;
	
	private UserAndRoleDTO mockUserAndRoleDto;
	
	private UserRoleMapSlimDTO mockUserRoleMapSlimDto;
	
	
    private List<UserRoleMap> userRoleMapList;
    
    private List<UserRoleMapSlimDTO> userRoleMapsSlimList;
    
    
	@BeforeEach
	void setUp() throws Exception {
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
		
		return mockUserDto;
	}
	
	@DisplayName("test for createUser method")
	@Test
	//@Order(2)
	void createUserTest() throws InvalidDataException, DuplicateResourceFound {

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
	void createUserWithRole() throws InvalidDataException, DuplicateResourceFound {

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
		Assertions.assertThrows(DuplicateResourceFoundException.class, ()->userService.createUser(mockUserDto));
		
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
}
