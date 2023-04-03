package com.numpyninja.lms.mappers;

import java.util.List;

import com.numpyninja.lms.dto.UserRoleProgramBatchSlimDto;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import org.mapstruct.Mapper;

import com.numpyninja.lms.dto.AssignmentDto;
import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.dto.UserAndRoleDTO;
import com.numpyninja.lms.dto.UserDto;

import com.numpyninja.lms.dto.UserRoleMapSlimDTO;
import com.numpyninja.lms.dto.UserRoleProgramBatchDto;
import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleMap;
import org.mapstruct.Mapping;

@Mapper(
	    componentModel = "spring"
	)
public interface UserMapper {

	UserDto userDto(User user);
	
	User user(UserDto userDto);
	
	List<UserDto> userDtos(List<User> users);
	
	
	//for user role creation
	User toUser(UserAndRoleDTO userAndRoleDto);
	
	UserRoleMap userRoleMap(UserRoleMapSlimDTO userRoleMapSlimDto);
	
	List<UserRoleMap> userRoleMapList(List<UserRoleMapSlimDTO> userRoleMapSlimDto);
	
	UserRoleMap userRoleMap(UserAndRoleDTO userAndRoleDto);
	
	List<UserRoleMap> touserRoleMapList(List<UserAndRoleDTO> userAndRoleDto);
	
	//UserRoleDTO userRoleDto(UserRoleMap userRoleMap);
	
	UserRoleMapSlimDTO toUserSlimRoleMapDto(UserRoleMap userRoleMap);

	@Mapping(source="batchId", target="batch.batchId")
	UserRoleProgramBatchMap toUserRoleProgramBatchMap(UserRoleProgramBatchSlimDto userRoleProgramBatchSlimDto);

	
	List<UserRoleProgramBatchDto> toUserRoleProgramBatchMapDtoList(List<UserRoleProgramBatchMap> UserProgBatch);
}
