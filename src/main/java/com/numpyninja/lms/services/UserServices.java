package com.numpyninja.lms.services;

import com.numpyninja.lms.config.UserIDGenerator;
import com.numpyninja.lms.dto.*;
import com.numpyninja.lms.entity.*;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.*;
import com.numpyninja.lms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServices {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserLoginRepository userLoginRepository;

	@Autowired
	UserRoleMapRepository userRoleMapRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserMapper userMapper;

	@Autowired
	ProgramRepository programRepository;

	@Autowired
	ProgBatchRepository progBatchRepository;

	@Autowired
	UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;

	@Autowired
	ProgramMapper programMapper;

	@Autowired
	BatchMapper batchMapper;

	@Autowired
	UserSkillRepository userSkillRepository;

	@Autowired
	UserSkillMapper userSkillMapper;

	@Autowired
	UserPictureRepository userPictureRepository;

	@Autowired
	UserPictureMapper userPictureMapper;

	private static final String ROLE_STUDENT = "R03";

	public List<UserDto> getAllUsers() {
		return userMapper.userDtos(userRepository.findAll());
		// return userRepository.findAll();
	}

	public UserAllDto getUserInfoById(String userId){
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

		List<UserRoleMap> userRoleMaps = userRoleMapRepository.findUserRoleMapsByUserUserId(userId);

		UserAllDto userAllDto = UserAllDto.builder()
				.userDto(userMapper.userDto(existingUser))
				.userRoleMaps(userMapper.toUserRoleMapSlimDtos(userRoleMaps))
				.build();

		List<UserRoleProgramBatchMap> userRoleProgramBatchMaps = userRoleProgramBatchMapRepository.findByUser_UserId(userId);
		if(!userRoleProgramBatchMaps.isEmpty()) {
			List<UserProgramBatchSlimDto> userProgramBatchSlimDtoList = new ArrayList<>();
			Map<Program,List<UserRoleProgramBatchMap>> convertedMap = userRoleProgramBatchMaps.stream()
					.collect(Collectors.groupingBy(UserRoleProgramBatchMap::getProgram));
			for (Map.Entry<Program,List<UserRoleProgramBatchMap>> entrySet : convertedMap.entrySet()) {
				UserProgramBatchSlimDto userProgramBatchSlimDto = UserProgramBatchSlimDto.builder()
						.programId(entrySet.getKey().getProgramId())
						.programName(entrySet.getKey().getProgramName())
						.batchSlimDto(batchMapper.toBatchSlimDtoList(entrySet.getValue()))
						.build();
				userProgramBatchSlimDtoList.add(userProgramBatchSlimDto);
			}
			userAllDto.setUserProgramBatchSlimDtos(userProgramBatchSlimDtoList);
		}

		List<UserSkill> userSkills = userSkillRepository.findByUserId(userId);
		if(!userSkills.isEmpty())
			userAllDto.setUserSkillSlimDtos(userSkillMapper.toUserSkillSlimDtoList(userSkills));

		List<UserPictureEntity> userPictureEntityList = userPictureRepository.findByUser_UserId(userId);
		if(!userPictureEntityList.isEmpty())
			userAllDto.setUserPictureSlimDtos(userPictureMapper.toUserPictureSlimDtoList(userPictureEntityList));

		return userAllDto;
	}
	@Transactional
	public UserDto createUserWithRole(UserAndRoleDTO newUserRoleDto)
			throws InvalidDataException, DuplicateResourceFoundException {
		User newUser = null;
		UserRoleMap newUserRoleMap = null;
		Role userRole = null;
		List<UserRoleMap> newUserRoleMapList = null;
		User createdUser = null;
		Date utilDate = new Date();

		if (newUserRoleDto != null) {

			/** Checking phone number to prevent duplicate entry **/
			List<User> userList = userRepository.findAll();
			if (userList.size() > 0) {
				boolean isPhoneNumberExists = checkDuplicatePhoneNumber(userList, newUserRoleDto.getUserPhoneNumber());
				if (isPhoneNumberExists) {
					throw new DuplicateResourceFoundException("Failed to create new User as phone number "
							+ newUserRoleDto.getUserPhoneNumber() + " already exists !!");
				}
			}
			/** Checking for valid TimeZone **/
			if (!isTimeZoneValid(newUserRoleDto.getUserTimeZone())) {
				throw new InvalidDataException("Failed to create user, as 'TimeZone' is invalid !! ");
			}
			/** Checking for valid Visa Status **/
			if (!isVisaStatusValid(newUserRoleDto.getUserVisaStatus())) {
				throw new InvalidDataException("Failed to create user, as 'Visa Status' is invalid !! ");
			}

			newUser = userMapper.toUser(newUserRoleDto);
			// System.out.println("new user " + newUser);

			newUser.setCreationTime(new Timestamp(utilDate.getTime()));
			newUser.setLastModTime(new Timestamp(utilDate.getTime()));

			/** Creating a new user **/
			createdUser = userRepository.save(newUser);

			// System.out.println("get USer role maps from newUserRoleDto" +
			// newUserRoleDto.getUserRoleMaps().toString());

			if (newUserRoleDto.getUserRoleMaps() != null) {
				for (int i = 0; i < newUserRoleDto.getUserRoleMaps().size(); i++) {
					String roleName = null;
					String roleId = null;
					String roleStatus = null;
					String userId = null;

					// System.out.println(newUserRoleDto.getUserRoleMaps().get(i).getRoleName());
					roleId = newUserRoleDto.getUserRoleMaps().get(i).getRoleId();
					// System.out.println("roleId " + roleId);
					Role roleUser = roleRepository.getById(roleId);

					roleStatus = newUserRoleDto.getUserRoleMaps().get(i).getUserRoleStatus();
					// System.out.println("roleStatus " + roleStatus);
					userId = createdUser.getUserId();
					// System.out.println("userId " + userId);

					newUserRoleMapList = userMapper.userRoleMapList(newUserRoleDto.getUserRoleMaps());
					newUserRoleMapList.get(i).setUserRoleStatus(roleStatus);

					newUserRoleMapList.get(i).setUser(createdUser);
					newUserRoleMapList.get(i).setRole(roleUser);
					newUserRoleMapList.get(i).setCreationTime(new Timestamp(utilDate.getTime()));
					newUserRoleMapList.get(i).setLastModTime(new Timestamp(utilDate.getTime()));
					UserRoleMap createdUserRole = userRoleMapRepository.save(newUserRoleMapList.get(i));

				}
			} else {
				throw new InvalidDataException("User Data not valid - Missing Role information");
			}

		} else {
			throw new InvalidDataException("User Data not valid ");
		}


		// UserRoleMap createdUserRole = userRoleMapRepository.save(newUserRoleMap);

		// How to return createdUSerRoleDTO

		UserDto createdUserdto = userMapper.userDto(createdUser);
		// UserRoleDTO createdUserRoleDto = userMapper.userDto(createdUser);
		return createdUserdto;
	}



	@Transactional
	public UserDto createUserLoginWithRole(UserLoginRoleDTO newUserLoginRoleDto) throws InvalidDataException, DuplicateResourceFoundException {
		User newUser = null;
		UserRoleMap newUserRoleMap = null;
		Role userRole = null;
		List<UserRoleMap> newUserRoleMapList = null;
		User createdUser = null;
		UserLogin createdUserLogin = null;
		Date utilDate = new Date();

		if (newUserLoginRoleDto != null) {

			/** Checking phone number to prevent duplicate entry **/
			List<User> userList = userRepository.findAll();
			if (userList.size() > 0) {
				boolean isPhoneNumberExists = checkDuplicatePhoneNumber(userList, newUserLoginRoleDto.getUserPhoneNumber());
				if (isPhoneNumberExists) {
					throw new DuplicateResourceFoundException("Failed to create new User as phone number "
							+ newUserLoginRoleDto.getUserPhoneNumber() + " already exists !!");
				}
			}
			/** Checking for valid TimeZone **/
			if (!isTimeZoneValid(newUserLoginRoleDto.getUserTimeZone())) {
				throw new InvalidDataException("Failed to create user, as 'TimeZone' is invalid !! ");
			}
			/** Checking for valid Visa Status **/
			if (!isVisaStatusValid(newUserLoginRoleDto.getUserVisaStatus())) {
				throw new InvalidDataException("Failed to create user, as 'Visa Status' is invalid !! ");
			}

			newUser = userMapper.toUser(newUserLoginRoleDto);
			// System.out.println("new user " + newUser);

			newUser.setCreationTime(new Timestamp(utilDate.getTime()));
			newUser.setLastModTime(new Timestamp(utilDate.getTime()));


			/** Creating a new user **/
			createdUser = userRepository.save(newUser);

			if (newUserLoginRoleDto.getUserRoleMaps() != null) {
				for (int i = 0; i < newUserLoginRoleDto.getUserRoleMaps().size(); i++) {
					String roleName = null;
					String roleId = null;
					String roleStatus = null;
					String userId = null;

					roleId = newUserLoginRoleDto.getUserRoleMaps().get(i).getRoleId();

					Role roleUser = roleRepository.getById(roleId);

					roleStatus = newUserLoginRoleDto.getUserRoleMaps().get(i).getUserRoleStatus();

					newUserRoleMapList = userMapper.userRoleMapList(newUserLoginRoleDto.getUserRoleMaps());
					newUserRoleMapList.get(i).setUserRoleStatus(roleStatus);

					newUserRoleMapList.get(i).setUser(createdUser);
					newUserRoleMapList.get(i).setRole(roleUser);
					newUserRoleMapList.get(i).setCreationTime(new Timestamp(utilDate.getTime()));
					newUserRoleMapList.get(i).setLastModTime(new Timestamp(utilDate.getTime()));
					UserRoleMap createdUserRole = userRoleMapRepository.save(newUserRoleMapList.get(i));

				}

			} else {
				throw new InvalidDataException("User Data not valid - Missing Role information");
			}

		} else {
			throw new InvalidDataException("User Data not valid ");
		}
		//UserLoginEmail
		if (newUserLoginRoleDto.getUserLogin() != null) {
			UserLoginDto userLoginDto = newUserLoginRoleDto.getUserLogin();
			UserLogin userLogin = userMapper.toUserLogin(userLoginDto);

			// Check for existing user logins with the same email
			Optional<UserLogin> existingUserLogins = userLoginRepository.findByUserLoginEmailIgnoreCase(userLogin.getUserLoginEmail());

			if (!existingUserLogins.isEmpty()) {
				// If there are existing user logins, check if any of them have the same password
				if (existingUserLogins.stream().anyMatch(existingUserLogin -> existingUserLogin.getPassword().equals(userLogin.getPassword()))) {
					throw new DuplicateResourceFoundException("Failed to create new UserLogin as email and password combination already exists!");
				}
				// If none of them have the same password, throw an exception for duplicate email
				throw new DuplicateResourceFoundException("Failed to create new UserLogin as email already exists!");
			}

			userLogin.setUserId(createdUser.getUserId());
			userLogin.setUser(createdUser);
			userLogin.setCreationTime(new Timestamp(utilDate.getTime()));
			userLogin.setLastModTime(new Timestamp(utilDate.getTime()));
			createdUserLogin = userLoginRepository.save(userLogin);
		} else {
			throw new InvalidDataException("User Data not valid - Email is missing");
		}

		// UserRoleMap createdUserRole = userRoleMapRepository.save(newUserRoleMap);

		// How to return createdUSerRoleDTO

		UserDto createdUserdto = userMapper.userDto(createdUser);
		// UserRoleDTO createdUserRoleDto = userMapper.userDto(createdUser);
		return createdUserdto;
	}

	public UserDto updateUser(UserDto updateuserDto, String userId)
			throws ResourceNotFoundException, InvalidDataException {
		User toBeupdatedUser = null;
		Date utilDate = new Date();

		if (userId == null) {
			throw new InvalidDataException("UserId cannot be blank/null");
		} else {
			Optional<User> userById = userRepository.findById(userId);

			if (userById.isEmpty()) {
				throw new ResourceNotFoundException("UserID: " + userId + " Not Found");
			}

			else {
				if (!isTimeZoneValid(updateuserDto.getUserTimeZone())) {
					throw new InvalidDataException("Failed to update user, as 'TimeZone' is invalid !! ");
				}
				if (!isVisaStatusValid(updateuserDto.getUserVisaStatus())) {
					throw new InvalidDataException("Failed to update user, as 'Visa Status' is invalid !! ");
				}

				toBeupdatedUser = userMapper.user(updateuserDto);
				if(StringUtils.hasLength(updateuserDto.getUserLinkedinUrl()))
					toBeupdatedUser.setUserLinkedinUrl(updateuserDto.getUserLinkedinUrl());
				else
					toBeupdatedUser.setUserLinkedinUrl(userById.get().getUserLinkedinUrl());

				if(StringUtils.hasLength(updateuserDto.getUserLocation()))
					toBeupdatedUser.setUserLocation(updateuserDto.getUserLocation());
				else
					toBeupdatedUser.setUserLocation(userById.get().getUserLocation());

				if(StringUtils.hasLength(updateuserDto.getUserEduPg()))
					toBeupdatedUser.setUserEduPg(updateuserDto.getUserEduPg());
				else
					toBeupdatedUser.setUserEduPg(userById.get().getUserEduPg());

				if(StringUtils.hasLength(updateuserDto.getUserEduUg()))
					toBeupdatedUser.setUserEduUg(updateuserDto.getUserEduUg());
				else
					toBeupdatedUser.setUserEduUg(userById.get().getUserEduUg());

				if(StringUtils.hasLength(updateuserDto.getUserComments()))
					toBeupdatedUser.setUserComments(updateuserDto.getUserComments());
				else
					toBeupdatedUser.setUserComments(userById.get().getUserComments());

				if(StringUtils.hasLength(updateuserDto.getUserMiddleName()))
					toBeupdatedUser.setUserMiddleName(updateuserDto.getUserMiddleName());
				else
					toBeupdatedUser.setUserMiddleName(userById.get().getUserMiddleName());


				toBeupdatedUser.setUserId(userId);
				toBeupdatedUser.setCreationTime(userById.get().getCreationTime());
				toBeupdatedUser.setLastModTime(new Timestamp(utilDate.getTime()));
			}

			User updatedUser = userRepository.save(toBeupdatedUser);
			UserDto updatedUserDto = userMapper.userDto(updatedUser);
			return updatedUserDto;
		}
	}

	public String updateUserRoleStatus(UserRoleMapSlimDTO updateUserRoleStatus, String userId)
			throws InvalidDataException {

		if (userId == null) {
			throw new InvalidDataException("UserId cannot be blank/null");
		}

		else {
			Optional<User> userById = userRepository.findById(userId);

			if (userById.isEmpty()) {
				throw new ResourceNotFoundException("UserID: " + userId + " Not Found");
			} else {

				List<UserRoleMap> existingUserRoles = userRoleMapRepository.findUserRoleMapsByUserUserId(userId);

				String roleIdToUpdate = updateUserRoleStatus.getRoleId();

				String roleStatusToUpdate = updateUserRoleStatus.getUserRoleStatus();
				List<String> roleIdList;
				boolean roleFound = false;
				for (int roleCount = 0; roleCount < existingUserRoles.size(); roleCount++) {

					String existingRoleId = existingUserRoles.get(roleCount).getRole().getRoleId();

					if (roleIdToUpdate.equals(existingRoleId)) {
						roleFound = true;

						// if role id exists - update role status
						Long userRoleId = existingUserRoles.get(roleCount).getUserRoleId();

						// using Update custom query
						userRoleMapRepository.updateUserRole(userRoleId, roleStatusToUpdate);
						break;
					}

				}

				if (!roleFound) {
					throw new ResourceNotFoundException(
							"RoleID: " + roleIdToUpdate + " not found for the " + "UserID: " + userId);
				}
			}

			return userId;
		}

	}

	/** Service method for Delete User **/
	public String deleteUser(String userId) throws ResourceNotFoundException {

		boolean userExists = userRepository.existsById(userId);

		if (!userExists) {
			throw new ResourceNotFoundException("UserID: " + userId + " doesnot exist ");
		} else {
			userRepository.deleteById(userId);
		}
		return userId;
	}

	private void validateUserRoleProgramBatchDtoForUser(UserRoleProgramBatchDto userRoleProgramBatchDto) {
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<UserRoleProgramBatchDto>> violations = validator.validate(userRoleProgramBatchDto);
		StringBuffer sb = new StringBuffer();
		violations.forEach(i -> {
			sb.append(i.getMessage());
			sb.append(" \n ");
		});

		if(StringUtils.hasLength(sb)){
			throw new InvalidDataException(sb.toString());
		}
	}

	private void assignUpdateUserRoleProgramBatch(User existingUser, Role existingUserRole, Program existingProgram,
												  Batch existingBatch, UserRoleProgramBatchSlimDto dto) {
		UserRoleProgramBatchMap userRoleProgramBatchMap;
		Optional<UserRoleProgramBatchMap> optionalMap = userRoleProgramBatchMapRepository
				.findByUser_UserIdAndRoleRoleIdAndProgram_ProgramIdAndBatch_BatchId
						(existingUser.getUserId(), existingUserRole.getRoleId(),
								existingProgram.getProgramId(), existingBatch.getBatchId());
		if (optionalMap.isEmpty()) {
			// assign Program/Batch mapping to user
			userRoleProgramBatchMap = userMapper.toUserRoleProgramBatchMap(dto);
			userRoleProgramBatchMap.setUser(existingUser);
			userRoleProgramBatchMap.setRole(existingUserRole);
			userRoleProgramBatchMap.setProgram(existingProgram);
			userRoleProgramBatchMap.setBatch(existingBatch);
			userRoleProgramBatchMap.setCreationTime(Timestamp.valueOf(LocalDateTime.now()));
			userRoleProgramBatchMap.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
		}
		else {
			// update existing Program/Batch mapping of user
			userRoleProgramBatchMap = optionalMap.get();
			userRoleProgramBatchMap.setUserRoleProgramBatchStatus(dto.getUserRoleProgramBatchStatus());
			userRoleProgramBatchMap.setLastModTime(Timestamp.valueOf(LocalDateTime.now()));
		}
		userRoleProgramBatchMap = userRoleProgramBatchMapRepository.save(userRoleProgramBatchMap);
	}

	public String assignUpdateUserRoleProgramBatchStatus(UserRoleProgramBatchDto userRoleProgramBatchDto,
														 String userId) {

		Boolean isBatchValid;
		StringBuffer message = new StringBuffer();
		String roleId = userRoleProgramBatchDto.getRoleId();
		Long programId = userRoleProgramBatchDto.getProgramId();
		List<UserRoleProgramBatchSlimDto> roleProgramBatchList = userRoleProgramBatchDto.getUserRoleProgramBatches();

		validateUserRoleProgramBatchDtoForUser(userRoleProgramBatchDto);

		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

		Role existingUserRole = roleRepository.findById(roleId)
				.orElseThrow(() -> new ResourceNotFoundException("Role", "Id", roleId));

		boolean isPresentUserAndRole = userRoleMapRepository.
				existsUserRoleMapByUser_UserIdAndRole_RoleIdAndUserRoleStatusEqualsIgnoreCase(userId, roleId,
						"Active");
		if (!isPresentUserAndRole) // Active User-Role mapping should be present
			throw new ResourceNotFoundException("User", "Role", roleId);

		// Active Program should be present
		Program existingProgram = programRepository.findProgramByProgramIdAndProgramStatusEqualsIgnoreCase(
						programId, "Active")
				.orElseThrow(() -> new ResourceNotFoundException("Program " + programId, "Program Status", "Active"));

		// User with roleId 'R03' i.e. Student should be assigned to single program/batch
		if (roleProgramBatchList.size() != 1 && ROLE_STUDENT.equals(roleId))
			throw new InvalidDataException("User with Role " + roleId + " can be assigned to single program/batch");

		int msgCount = 0;
		for(UserRoleProgramBatchSlimDto dto : roleProgramBatchList) {

			//Active Program-Batch mapping should be present
			Integer batchId = dto.getBatchId();
			Optional<Batch> optionalBatch = progBatchRepository.findBatchByBatchIdAndProgram_ProgramIdAndBatchStatusEqualsIgnoreCase
					(batchId, programId, "Active");

			if(optionalBatch.isPresent())
				isBatchValid = true;
			else
				isBatchValid = false;

			if (isBatchValid) {
				if (ROLE_STUDENT.equals(roleId)) { // Validations only for Student users
					/* Check for existing assigned program/batch with Active status for given user */
					Optional<UserRoleProgramBatchMap> optionalExistingMap = userRoleProgramBatchMapRepository
							.findByUser_UserIdAndRoleRoleIdAndUserRoleProgramBatchStatusEqualsIgnoreCase
									(userId, roleId, "Active");

					if (optionalExistingMap.isPresent()) {
						UserRoleProgramBatchMap existingMap = optionalExistingMap.get();
						Long existingProgramId = existingMap.getProgram().getProgramId();
						Integer existingBatchId = existingMap.getBatch().getBatchId();

						/* Check whether received request for another program OR same program with another batch */
						if ((existingProgramId != programId) || (existingBatchId != batchId))
							throw new InvalidDataException
									("Please deactivate User from existing program/batch and then activate for another program/batch");
						else
							assignUpdateUserRoleProgramBatch(existingUser, existingUserRole, existingProgram, optionalBatch.get(), dto);
					}
					else
						assignUpdateUserRoleProgramBatch(existingUser, existingUserRole, existingProgram, optionalBatch.get(), dto);
				}
				else
					assignUpdateUserRoleProgramBatch(existingUser, existingUserRole, existingProgram, optionalBatch.get(), dto);
			}
			else {
				message.append(String.format("%s not found with %s for %s ","Batch " + batchId, "Status as Active",
						"Program " + programId));
				message.append(" \n ");
				msgCount++;
			}
		}
		if (StringUtils.hasLength(message.toString())) {
			if (ROLE_STUDENT.equals(roleId))
				throw new InvalidDataException(message.toString());
			else {
				if (msgCount < roleProgramBatchList.size())
					return "User " + userId + " has failed for" + " - " + message;
				else
					throw new InvalidDataException(message.toString());
			}
		}

		return "User " + userId + " has been successfully assigned to Program/Batch(es)";
	}

	/** Check for already existing phone number **/
	private boolean checkDuplicatePhoneNumber(List<User> userList, long phoneNumber) {
		boolean isUserPresent = false;

		for (User user : userList) {
			if (user.getUserPhoneNumber() == phoneNumber) {
				isUserPresent = true;
				break;
			}
		}
		return isUserPresent;
	}

	private boolean isTimeZoneValid(String timeZone) {
		Boolean isTimeZoneValid = false;
		List<String> timeZoneList = new ArrayList<String>(List.of("PST", "MST", "CST", "EST", "IST"));

		for (String itr : timeZoneList) {
			if (itr.equalsIgnoreCase(timeZone)) {
				isTimeZoneValid = true;
				break;
			}
		}
		return isTimeZoneValid;

	}

	private boolean isVisaStatusValid(String visa) {
		Boolean isVisaStatusValid = false;
		List<String> visaStatusList = new ArrayList<String>(List.of("Not-Specified", "NA", "GC-EAD", "H4-EAD", "H4",
				"H1B", "Canada-EAD", "Indian-Citizen", "US-Citizen", "Canada-Citizen"));
		for (String visaStatus : visaStatusList) {
			if (visaStatus.equalsIgnoreCase(visa)) {
				isVisaStatusValid = true;
			}
		}
		return isVisaStatusValid;
	}

	public List<Object> getAllStaff()
	{
		List<Object> result=userRepository.getAllStaffList();
		if(!(result.size()<=0))
		{
			//return (userMapper.toUserStaffDTO(result));
			return result;
		}else
		{
			throw new ResourceNotFoundException("No staff data is available in database");
		}
	}
	
	
//get users by batchid
	public List<UserDto> getUserByProgramBatch(Integer batchid) {
	
	Batch batch= progBatchRepository.findById(batchid)
				.orElseThrow(() -> new ResourceNotFoundException("batchid " + batchid + " not found"));
	
	List<UserRoleProgramBatchMap> userRoleProgramBatchMapList = userRoleProgramBatchMapRepository.findByBatch_BatchId(batchid);
	
	if (userRoleProgramBatchMapList.isEmpty()) 
	{
		   throw new ResourceNotFoundException("No Users found for the given Batch ID: " + batchid);
	}
	
	  List<UserDto> userDtoList = userRoleProgramBatchMapList.stream()
	    .map(UserRoleProgramBatchMap::getUser)
	    .map(user -> userMapper.userDtos(Arrays.asList(user)).get(0))
	    .collect(Collectors.toList());

	  return userDtoList;
	
}
	
	
	
	
	public List<UserDto> getUsersByProgram(Long programId) {
		Program program = programRepository.findById(programId)
				.orElseThrow(() -> new ResourceNotFoundException("programId " + programId + " not found"));
		List<UserRoleProgramBatchMap> userRoleProgramBatchMapList = userRoleProgramBatchMapRepository.findByProgram_ProgramId(programId);
		if (userRoleProgramBatchMapList.isEmpty()) {
			throw new ResourceNotFoundException("No Users found for the given program ID: " + programId);
		}
		// Use the UserMapper to directly map each User object to its corresponding UserDto object
		List<UserDto> userDtoList = userRoleProgramBatchMapList.stream()
				.map(UserRoleProgramBatchMap::getUser)
				.map(user -> userMapper.userDtos(Arrays.asList(user)).get(0))
				.collect(Collectors.toList());
		return userDtoList;
	}
	
	
	
	/*
	 * public UserDto getAllUsersById(String Id) throws ResourceNotFoundException {
	 * Optional<User> userById = userRepository.findById(Id); if(userById.isEmpty())
	 * { throw new ResourceNotFoundException("User Id " + Id +" not found"); } else
	 * { UserDto userDto = userMapper.userDto(userById.get()); return userDto; } }





	
	
	
	
	
	
	/**
	 * Check if the code below this comment are needed or not from front end. - The
	 * controller endpoints for these are commented out for now.
>>>>>>> LMSPhase2
	 */

/*
	// Displays Users Info with their user status, role
	public List<UserRoleMap> getAllUsersWithRoles() {
		// List<UserRoleMap> list = userRoleMapRepository.findAll();
		return userRoleMapRepository.findAll();
	}
	public UserDto createUser(UserDto newUserDto) throws InvalidDataException, DuplicateResourceFoundException {
		User newUser = null;
		Date utilDate = new Date();
		if (newUserDto != null) {
			List<User> userList = userRepository.findAll();
			if (userList.size() > 0) {
				boolean isPhoneNumberExists = checkDuplicatePhoneNumber(userList, newUserDto.getUserPhoneNumber());
				if (isPhoneNumberExists) {
					throw new DuplicateResourceFoundException("Failed to create new User as phone number "
							+ newUserDto.getUserPhoneNumber() + " already exists !!");
				}
			}
			if (!isTimeZoneValid(newUserDto.getUserTimeZone())) {
				throw new InvalidDataException("Failed to create user, as 'TimeZone' is invalid !! ");
			}
			if (!isVisaStatusValid(newUserDto.getUserVisaStatus())) {
				throw new InvalidDataException("Failed to create user, as 'Visa Status' is invalid !! ");
			}
			newUser = userMapper.user(newUserDto);
			newUser.setCreationTime(new Timestamp(utilDate.getTime()));
			newUser.setLastModTime(new Timestamp(utilDate.getTime()));
		} else {
			throw new InvalidDataException("User Data not valid ");
		}
		User createdUser = userRepository.save(newUser);
		UserDto createdUserdto = userMapper.userDto(createdUser);
		return createdUserdto;
	}
	public List<User> getAllUsersByRole(String roleName) {
		return userRoleMapRepository.findUserRoleMapsByRoleRoleName(roleName).stream()
				.map(userRoleMap -> userRoleMap.getUser()).collect(Collectors.toList());
	}
	public UserDto updateUserWithRole(UserAndRoleDTO updateUserRoleDto, String userId) throws InvalidDataException {
		User toBeupdatedUser = null;
		Date utilDate = new Date();
		List<UserRoleMap> UpdatedUserRoleMapList = null;
		if (userId == null) {
			throw new InvalidDataException("UserId cannot be blank/null");
		}
		else {
			Optional<User> userById = userRepository.findById(userId);
			// System.out.println("updateUserRoleDto " + updateUserRoleDto);
			if (userById.isEmpty()) {
				throw new ResourceNotFoundException("UserID: " + userId + " Not Found");
			} else {
				if (!isTimeZoneValid(updateUserRoleDto.getUserTimeZone())) {
					throw new InvalidDataException("Failed to update user, as 'TimeZone' is invalid !! ");
				}
				if (!isVisaStatusValid(updateUserRoleDto.getUserVisaStatus())) {
					throw new InvalidDataException("Failed to update user, as 'Visa Status' is invalid !! ");
				}
				toBeupdatedUser = userMapper.toUser(updateUserRoleDto);
				toBeupdatedUser.setUserId(userId);
				toBeupdatedUser.setCreationTime(userById.get().getCreationTime());
				toBeupdatedUser.setLastModTime(new Timestamp(utilDate.getTime()));
			}
			User updatedUser = userRepository.save(toBeupdatedUser);
			// Update Role Info
			List<UserRoleMap> existingUserRoles = userRoleMapRepository.findUserRoleMapsByUserUserId(userId);
			// System.out.println("existingUserRoles " + existingUserRoles);
			if (existingUserRoles != null) {
				for (int userRoleCnt = 0; userRoleCnt <= existingUserRoles.size(); userRoleCnt++) {
					Long existingUserRoleId = existingUserRoles.get(userRoleCnt).getUserRoleId();
					String existingRoleId = existingUserRoles.get(userRoleCnt).getRole().getRoleId();
					if (updateUserRoleDto.getUserRoleMaps() != null) {
						for (int i = 0; i < updateUserRoleDto.getUserRoleMaps().size(); i++) {
							String roleName = null;
							String roleId = null;
							String roleStatus = null;
							// String userId = null;
							// System.out.println(newUserRoleDto.getUserRoleMaps().get(i).getRoleName());
							roleId = updateUserRoleDto.getUserRoleMaps().get(i).getRoleId();
							// System.out.println("roleId " + roleId);
							Role roleUser = roleRepository.getById(roleId);
							// uncommented the below line
							roleStatus = updateUserRoleDto.getUserRoleMaps().get(i).getUserRoleStatus();
							System.out.println("roleStatus " + roleStatus);
							// userId = createdUser.getUserId();
							// System.out.println("userId " + userId);
							UpdatedUserRoleMapList = userMapper.userRoleMapList(updateUserRoleDto.getUserRoleMaps());
							System.out.println("UpdatedUserRoleMapList " + UpdatedUserRoleMapList);
							if (roleId == existingRoleId) {
								UpdatedUserRoleMapList.get(i).setUserRoleId(existingUserRoleId);
							}
							UpdatedUserRoleMapList.get(i).setUserRoleStatus(roleStatus);
							UpdatedUserRoleMapList.get(i).setUser(updatedUser);
							UpdatedUserRoleMapList.get(i).setRole(roleUser);
							UpdatedUserRoleMapList.get(i).setCreationTime(new Timestamp(utilDate.getTime()));
							UpdatedUserRoleMapList.get(i).setLastModTime(new Timestamp(utilDate.getTime()));
							UserRoleMap updatedUserRole = userRoleMapRepository.save(UpdatedUserRoleMapList.get(i));
						}
					} else {
						throw new InvalidDataException("User Data not valid - Missing Role information");
					}
				}
			}
			UserDto updatedUserDto = userMapper.userDto(updatedUser);
			return updatedUserDto;
		}
	}
*/

}
