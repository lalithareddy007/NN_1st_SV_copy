package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.entity.AssignmentSubmit;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", uses={UserMapper.class,AssignmentMapper.class})
public interface AssignmentSubmitMapper {

    AssignmentSubmit INSTANCE= Mappers.getMapper(AssignmentSubmit.class);

    @Mapping(source="assignmentSubmit.assignment.assignmentId", target="assignmentId")
    @Mapping(source="assignmentSubmit.user.userId", target="userId")
    AssignmentSubmitDTO toAssignmentSubmitDTO(AssignmentSubmit assignmentSubmit);

    @InheritInverseConfiguration
    AssignmentSubmit toAssignmentSubmit(AssignmentSubmitDTO assignmentSubmitDTO);

    List<AssignmentSubmitDTO> toAssignmentSubmitDTOList(List<AssignmentSubmit> assignmentSubmitList);

}
