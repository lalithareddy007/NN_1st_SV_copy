package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.numpyninja.lms.config.ValidateStatus;

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

    //custom annotation to validate status( accepts only "Active" and "Inactive")
    @ValidateStatus
    @JsonProperty("userBatchStatus")
    private String userRoleProgramBatchStatus;

}
