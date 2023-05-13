package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.UserDto;
import com.numpyninja.lms.entity.UserLogin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface UserLoginMapper {
    UserLoginMapper INSTANCE = Mappers.getMapper(UserLoginMapper.class);

    @Mapping(source = "userLogin.user.userFirstName", target = "userFirstName")
    @Mapping(source = "userLogin.user.userLastName", target = "userLastName")
    @Mapping(source = "userLogin.user.userMiddleName", target = "userMiddleName")
    @Mapping(source = "userLogin.user.userPhoneNumber", target = "userPhoneNumber")
    @Mapping(source = "userLogin.user.userTimeZone", target = "userTimeZone")
    @Mapping(source = "userLogin.user.userLinkedinUrl", target = "userLinkedinUrl")
    @Mapping(source = "userLogin.user.userLocation", target = "userLocation")
    @Mapping(source = "userLogin.user.userEduUg", target = "userEduUg")
    @Mapping(source = "userLogin.user.userEduPg", target = "userEduPg")
    @Mapping(source = "userLogin.user.userComments", target = "userComments")
    @Mapping(source = "userLogin.user.userVisaStatus", target = "userVisaStatus")
    UserDto toUserDto(UserLogin userLogin);

    List<UserDto> toUserDTOs(List<UserLogin> userLogins);
}
