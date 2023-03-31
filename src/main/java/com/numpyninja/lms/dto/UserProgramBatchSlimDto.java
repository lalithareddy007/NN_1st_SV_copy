package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProgramBatchSlimDto {

    private Long programId;

    private String programName;

    @JsonProperty("batches")
    private List<BatchSlimDto> batchSlimDto;
}
