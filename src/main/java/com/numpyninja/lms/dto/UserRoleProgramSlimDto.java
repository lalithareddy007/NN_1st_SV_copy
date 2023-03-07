package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleProgramSlimDto {

    @NotNull(message = "Program Id is mandatory")
    @JsonProperty("programId")
    private Long programId;

    @Valid
    @NotEmpty(message = "User-Role-Program-Batch Info is mandatory")
    @JsonProperty("userRoleProgramBatches")
    private List<UserRoleProgramBatchSlimDto> userRoleProgramBatches;
}
