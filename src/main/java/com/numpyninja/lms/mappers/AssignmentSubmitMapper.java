package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.AssignmentSubmitDTO;
import com.numpyninja.lms.entity.AssignmentSubmit;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AssignmentSubmitMapper {

    AssignmentSubmitDTO toAssignmentSubmitDTO(AssignmentSubmit assigmentSubmit);

    AssignmentSubmit toAssignmentSubmit(AssignmentSubmitDTO assigmentSubmitDTO);

    List<AssignmentSubmitDTO> toAssignmentSubmitDTOList(List<AssignmentSubmit> assignmentSubmitList);

}
