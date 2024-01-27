package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.UserRoleProgramBatchDto;
import com.numpyninja.lms.dto.UserRoleProgramBatchMapDto;
import com.numpyninja.lms.dto.UserRoleProgramBatchSlimDto;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Mapper(componentModel = "spring", uses={UserMapper.class,RoleMapper.class,ProgramMapper.class,BatchMapper.class})
//public interface UserRoleProgramBatchMapMapper {
//
//    UserRoleProgramBatchMapMapper INSTANCE = Mappers.getMapper(UserRoleProgramBatchMapMapper.class);
//
//    @Mapping(source="userRoleProgramBatchMap.user.userId", target="userId")
//    @Mapping(source="userRoleProgramBatchMap.role.roleId", target="roleId")
//    @Mapping(source="userRoleProgramBatchMap.program.programId", target="programId")
//    @Mapping(source="userRoleProgramBatchMap.batch.batchId", target="batchId")
//    UserRoleProgramBatchMapDto toUserRoleProgramBatchMapDto(UserRoleProgramBatchMap userRoleProgramBatchMap);
//
//    @InheritInverseConfiguration
//    UserRoleProgramBatchMap toUserRoleProgramBatchMap(UserRoleProgramBatchMapDto userRoleProgramBatchMapDto);
//
//    List<UserRoleProgramBatchMapDto> toUserRoleProgramBatchMapDtoList(List<UserRoleProgramBatchMap> userRoleProgramBatchMaps);
//
//    List<UserRoleProgramBatchMap> toUserRoleProgramBatchMapList(List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos);








@Mapper(componentModel = "spring", uses={UserMapper.class,RoleMapper.class,ProgramMapper.class,BatchMapper.class})
public interface UserRoleProgramBatchMapMapper {

    com.numpyninja.lms.mappers.UserRoleProgramBatchMapMapper INSTANCE = Mappers.getMapper(com.numpyninja.lms.mappers.UserRoleProgramBatchMapMapper.class);

    @Mapping(source="userRoleProgramBatchMap.user.userId", target="userId")
    @Mapping(source="userRoleProgramBatchMap.role.roleId", target="roleId")
    @Mapping(source="userRoleProgramBatchMap.program.programId", target="programId")
    @Mapping(source="userRoleProgramBatchMap.batch.batchId", target="batchId")
    UserRoleProgramBatchMapDto toUserRoleProgramBatchMapDto(UserRoleProgramBatchMap userRoleProgramBatchMap);

    @InheritInverseConfiguration
    UserRoleProgramBatchMap toUserRoleProgramBatchMap(UserRoleProgramBatchMapDto userRoleProgramBatchMapDto);

    List<UserRoleProgramBatchMapDto> toUserRoleProgramBatchMapDtoList(List<UserRoleProgramBatchMap> userRoleProgramBatchMaps);

    List<UserRoleProgramBatchMap> toUserRoleProgramBatchMapList(List<UserRoleProgramBatchMapDto> userRoleProgramBatchMapDtos);

   }
