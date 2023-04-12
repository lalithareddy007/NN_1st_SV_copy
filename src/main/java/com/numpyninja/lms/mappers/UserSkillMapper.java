package com.numpyninja.lms.mappers;


import com.numpyninja.lms.dto.UserSkillDTO;
import com.numpyninja.lms.dto.UserSkillSlimDto;
import com.numpyninja.lms.entity.UserSkill;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring" , uses = {UserMapper.class,SkillMasterMapper.class})
public interface UserSkillMapper {
    UserSkillMapper INSTANCE = Mappers.getMapper(UserSkillMapper.class);

    @Mapping(source="user.userId" ,target="userId")
    @Mapping(source="skill.skillId",target="skillId")
    @Mapping ( source = "skill.skillName", target = "skillName")
    UserSkillDTO toUserSkillDTO(UserSkill savedEntity);
   @InheritInverseConfiguration
    UserSkill toUserSkillEntity( UserSkillDTO userSkillDTO);
   List<UserSkillDTO> toUserSkillDTOList(List<UserSkill> userSkillEntities);
   List< UserSkill> toUserSkillEntityList(List<UserSkillDTO> UserSkillDTOs);

    @Mappings(value = {
            @Mapping(source="skill.skillId", target="skillId"),
            @Mapping(source="skill.skillName", target="skillName")
    })
    UserSkillSlimDto toUserSkillSlimDto(UserSkill userSkill);

    List<UserSkillSlimDto> toUserSkillSlimDtoList(List<UserSkill> userSkillList);
}
