package com.numpyninja.lms.mappers;

import java.util.List;

import com.numpyninja.lms.dto.ClassRecordingDTO;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.numpyninja.lms.dto.ClassDto;
import com.numpyninja.lms.entity.Class;


@Mapper(componentModel = "spring", uses= {BatchMapper.class,UserMapper.class})
public interface ClassScheduleMapper {
	ClassScheduleMapper INSTANCE = Mappers.getMapper(ClassScheduleMapper.class);
	
	@Mapping(source="batchInClass.batchId",target="batchId")
	@Mapping(source="staffInClass.userId", target="classStaffId")
	ClassDto toClassSchdDTO(Class savedEntity);
		
		@InheritInverseConfiguration
		Class toClassScheduleEntity(ClassDto classSchdDTO);
		 
	   	List<ClassDto> toClassScheduleDTOList(List<Class> classSchdEntites);
		 
		 List<Class> toClassScheduleEntityList(List<ClassDto> classSchdDTOs);

		 List<ClassRecordingDTO> toClassRecordingDtoList(List<Class> ClassRecords);

}
