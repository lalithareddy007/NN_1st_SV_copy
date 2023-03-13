package com.numpyninja.lms.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import com.numpyninja.lms.dto.AttendanceDto;
import com.numpyninja.lms.dto.UserPictureEntityDTO;
//import com.numpyninja.lms.dto.UserPictureEntityDto;
import com.numpyninja.lms.entity.Attendance;
import com.numpyninja.lms.entity.UserPictureEntity;


	
	
	
	@Component
	@Mapper(componentModel = "spring",uses={ UserMapper.class})
	public interface UserPictureMapper {

		UserPictureMapper INSTANCE = Mappers.getMapper(UserPictureMapper.class);
	
		//changes made 
		@Mapping ( source = "userpicturedto.userId", target = "user.userId" )
		UserPictureEntity toUserPictureEntity(UserPictureEntityDTO userpicturedto);	
		
		
		
		@Mapping(source = "userpictureentity.user.userId",target ="userId")
		UserPictureEntityDTO toUserPictureEntityDto(UserPictureEntity pictureindb);

		
		

	}

