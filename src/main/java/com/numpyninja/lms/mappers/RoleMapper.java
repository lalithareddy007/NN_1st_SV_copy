package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.RoleDto;
import com.numpyninja.lms.entity.Role;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper
{
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto toRoleDto(Role role);

    @InheritInverseConfiguration
    Role torole(RoleDto roleDto);

    List<Role> toRoleList(List<RoleDto> roleDtos);

    List<RoleDto> toRoleDtoList(List<Role> roles);
}
