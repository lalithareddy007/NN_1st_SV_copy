package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.UserPictureEntityDTO;
import com.numpyninja.lms.dto.UserPictureSlimDto;
import com.numpyninja.lms.entity.UserPictureEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;


	
	
	
	@Component
	@Mapper(componentModel = "spring",uses={ UserMapper.class})
	public interface UserPictureMapper {

		UserPictureMapper INSTANCE = Mappers.getMapper(UserPictureMapper.class);
	
		//changes made 
		@Mapping ( source = "userpicturedto.userId", target = "user.userId" )
		UserPictureEntity toUserPictureEntity(UserPictureEntityDTO userpicturedto);	
		
		
		
		@Mapping(source = "userpictureentity.user.userId",target ="userId")
		UserPictureEntityDTO toUserPictureEntityDto(UserPictureEntity userpictureentity);


		UserPictureSlimDto toUserPictureSlimDto(UserPictureEntity userPictureEntity);

		List<UserPictureSlimDto> toUserPictureSlimDtoList(List<UserPictureEntity> userPictureEntityList);
		

	}

