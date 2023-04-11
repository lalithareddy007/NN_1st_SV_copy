package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BatchSlimDto {

    private Integer batchId;

    private String batchName;

    @JsonProperty("userBatchStatus")
    private String userRoleProgramBatchStatus;

}
