package com.numpyninja.lms.mappers;

import java.util.List;

import com.numpyninja.lms.dto.*;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import org.mapstruct.Mapper;

import com.numpyninja.lms.entity.Class;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserRoleMap;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

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

	@Mapping(source="role.roleId", target="roleId")
	UserRoleMapSlimDTO toUserSlimRoleMapDto(UserRoleMap userRoleMap);

	@Mapping(source="batchId", target="batch.batchId")
	UserRoleProgramBatchMap toUserRoleProgramBatchMap(UserRoleProgramBatchSlimDto userRoleProgramBatchSlimDto);

	List<UserRoleMapSlimDTO> toUserRoleMapSlimDtos(List<UserRoleMap> userRoleMaps);

}
