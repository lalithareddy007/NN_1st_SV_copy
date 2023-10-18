package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.*;
import com.numpyninja.lms.entity.User;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.entity.UserRoleMap;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto userDto(User user);

    User user(UserDto userDto);

    List<UserDto> userDtos(List<User> users);


    //for user role creation
    User toUser(UserAndRoleDTO userAndRoleDto);

    User toUser(UserLoginRoleDTO userLoginRoleDTO);

    UserLogin toUserLogin(UserLoginDto userLogin);


    UserRoleMap userRoleMap(UserRoleMapSlimDTO userRoleMapSlimDto);

    List<UserRoleMap> userRoleMapList(List<UserRoleMapSlimDTO> userRoleMapSlimDto);

    UserRoleMap userRoleMap(UserAndRoleDTO userAndRoleDto);
    
    UserRoleMap userRole(UserRoleIdDTO updateRoleId);
    
    
    

    List<UserRoleMap> touserRoleMapList(List<UserAndRoleDTO> userAndRoleDto);

    //UserRoleDTO userRoleDto(UserRoleMap userRoleMap);

    @Mapping(source = "role.roleId", target = "roleId")
    UserRoleMapSlimDTO toUserSlimRoleMapDto(UserRoleMap userRoleMap);

    @Mapping(source = "batchId", target = "batch.batchId")
    UserRoleProgramBatchMap toUserRoleProgramBatchMap(UserRoleProgramBatchSlimDto userRoleProgramBatchSlimDto);


    List<UserRoleProgramBatchDto> toUserRoleProgramBatchMapDtoList(List<UserRoleProgramBatchMap> UserProgBatch);

    List<UserRoleMapSlimDTO> toUserRoleMapSlimDtos(List<UserRoleMap> userRoleMaps);


}
