package com.numpyninja.lms.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.numpyninja.lms.dto.UserAndRoleDTO;
import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.dto.UserRoleMapSlimDTO;
import com.numpyninja.lms.entity.Role;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleMap;
import com.numpyninja.lms.exception.DuplicateResourceFoundException;
import com.numpyninja.lms.exception.InvalidDataException;
import com.numpyninja.lms.exception.ResourceNotFoundException;
import com.numpyninja.lms.mappers.UserMapper;
import com.numpyninja.lms.repository.RoleRepository;
import com.numpyninja.lms.repository.UserRepository;
import com.numpyninja.lms.repository.UserRoleMapRepository;

@Service
public class UserServices {

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserRoleMapRepository userRoleMapRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserMapper userMapper;

	public List<UserDto> getAllUsers() {
		return userMapper.userDtos(userRepository.findAll());
		// return userRepository.findAll();
	}

	public List<UserRoleMap> getUserInfoById(String Id) throws ResourceNotFoundException {
		Optional<User> userById = userRepository.findById(Id);
		if (userById.isEmpty()) {
			throw new ResourceNotFoundException("User Id " + Id + " not found");
		} else {
			List<UserRoleMap> userroleMap = userRoleMapRepository.findUserRoleMapsByUserUserId(Id);
			// System.out.println("userroleMap " + userroleMap);
			return userroleMap;
		}
	}

	// Displays Users Info with their user status, role, batch and program info
	public List<UserRoleMap> getAllUsersWithRoles() {
		// List<UserRoleMap> list = userRoleMapRepository.findAll();
		return userRoleMapRepository.findAll();
	}

	public List<UserRoleMap> getUsersForProgram(Long programId) {
		List<UserRoleMap> list = userRoleMapRepository.findUserRoleMapsByBatchesProgramProgramId(programId);

		return list.stream().map(userRoleMap -> {
			userRoleMap.getBatches().removeIf(batch -> batch.getProgram().getProgramId() == programId);
			return userRoleMap;
		}).collect(Collectors.toList());
	}

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

	/**
	 * Check if the code below this comment are needed or not from front end. - The
	 * controller endpoints for these are commented out for now.
	 */

	public UserDto createUser(UserDto newUserDto) throws InvalidDataException, DuplicateResourceFoundException {
		User newUser = null;
		Date utilDate = new Date();

		if (newUserDto != null) {

			/** Checking phone number to prevent duplicate entry **/
			List<User> userList = userRepository.findAll();
			if (userList.size() > 0) {
				boolean isPhoneNumberExists = checkDuplicatePhoneNumber(userList, newUserDto.getUserPhoneNumber());
				if (isPhoneNumberExists) {
					throw new DuplicateResourceFoundException("Failed to create new User as phone number "
							+ newUserDto.getUserPhoneNumber() + " already exists !!");
				}
			}
			/** Checking for valid TimeZone **/
			if (!isTimeZoneValid(newUserDto.getUserTimeZone())) {
				throw new InvalidDataException("Failed to create user, as 'TimeZone' is invalid !! ");
			}
			/** Checking for valid Visa Status **/
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

	/*
	 * public UserDto getAllUsersById(String Id) throws ResourceNotFoundException {
	 * Optional<User> userById = userRepository.findById(Id); if(userById.isEmpty())
	 * { throw new ResourceNotFoundException("User Id " + Id +" not found"); } else
	 * { UserDto userDto = userMapper.userDto(userById.get()); return userDto; } }
	 */

}
