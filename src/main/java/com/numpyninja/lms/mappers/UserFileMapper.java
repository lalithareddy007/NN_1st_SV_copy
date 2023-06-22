package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.UserFileEntityDTO;
import com.numpyninja.lms.dto.UserFileSlimDto;
import com.numpyninja.lms.entity.UserFileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;


	
	
	
	@Component
	@Mapper(componentModel = "spring",uses={ UserMapper.class})
	public interface UserFileMapper {

		UserFileMapper INSTANCE = Mappers.getMapper(UserFileMapper.class);
	
		//changes made 
		@Mapping ( source = "userId", target = "user.userId" )
        UserFileEntity toUserFileEntity(UserFileEntityDTO userFiledto);
		
		
		
		@Mapping(source = "user.userId",target ="userId")
        UserFileEntityDTO toUserFileEntityDto(UserFileEntity userFileentity);


		UserFileSlimDto toUserFileSlimDto(UserFileEntity userFileEntity);

		List<UserFileSlimDto> toUserFileSlimDtoList(List<UserFileEntity> userFileEntityList);
		

	}

