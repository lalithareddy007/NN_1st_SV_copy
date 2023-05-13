package com.numpyninja.lms.mappers;

import com.numpyninja.lms.dto.BatchDTO;
import com.numpyninja.lms.dto.BatchSlimDto;
import com.numpyninja.lms.entity.Batch;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring", uses = ProgramMapper.class)
public interface BatchMapper {
    BatchMapper INSTANCE = Mappers.getMapper(BatchMapper.class);

    @Mapping(source = "batch.program.programId", target = "programId")
    @Mapping(source = "batch.program.programName", target = "programName")
    BatchDTO toBatchDTO(Batch batch);

    @Mapping(source = "dto.programId", target = "program.programId")
    Batch toBatch(BatchDTO dto);

    List<BatchDTO> toBatchDTOs(List<Batch> baches);

    @Mappings(value = {
            @Mapping(source = "batch.batchId", target = "batchId"),
            @Mapping(source = "batch.batchName", target = "batchName"),
            @Mapping(source = "userRoleProgramBatchStatus", target = "userRoleProgramBatchStatus")
    })
    BatchSlimDto toBatchSlimDto(UserRoleProgramBatchMap userRoleProgramBatchMap);

    List<BatchSlimDto> toBatchSlimDtoList(List<UserRoleProgramBatchMap> userRoleProgramBatchMapList);

}
