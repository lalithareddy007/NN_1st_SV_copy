package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleProgramBatchSlimDto {

    @NotNull(message = "Batch Id is mandatory")
    @JsonProperty("batchId")
    private Integer batchId;

    @NotEmpty(message = "User-Role-Program-Batch Status is Mandatory")
    @JsonProperty("userRoleProgramBatchStatus")
    private String userRoleProgramBatchStatus;

}
