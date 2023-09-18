package com.numpyninja.lms.mappers;

import java.util.List;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.numpyninja.lms.dto.ProgramDTO;
import com.numpyninja.lms.entity.Program;


@Mapper(componentModel = "spring")
public interface ProgramMapper {
	ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);


    ProgramDTO toProgramDTO(Program savedEntity);
	
	//@InheritInverseConfiguration
	Program toProgramEntity(ProgramDTO progDTO);
	 
   	List<ProgramDTO> toProgramDTOList(List<Program> programEntities);
	 
	 List<Program> toPogramEntityList(List<ProgramDTO> ProgramDTOs);

}
