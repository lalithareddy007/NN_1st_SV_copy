package com.numpyninja.lms.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.numpyninja.lms.dto.ProgramWithUsersDTO;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;

@Mapper(componentModel = "Spring")
public interface ProgramWithUsersMapper {
	ProgramWithUsersMapper INSTANCE = Mappers.getMapper(ProgramWithUsersMapper.class);
	
	List<ProgramWithUsersDTO> toProgramsWithUsers(List<UserRoleProgramBatchMap> userRoleProgramMap);
	

}
