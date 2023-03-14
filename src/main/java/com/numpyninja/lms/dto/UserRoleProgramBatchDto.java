package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleProgramBatchDto {

    @JsonProperty("userId")
    private String userId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotEmpty(message = "Role Id is mandatory")
    @JsonProperty("roleId")
    private String roleId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "Program Id is mandatory")
    @DecimalMin(value = "1", message = "Program Id must be greater than or equal to 1")
    @JsonProperty("programId")
    private Long programId;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotEmpty(message = "User-Role-Program-Batch Info is mandatory")
    @JsonProperty("userRoleProgramBatches")
    private List<UserRoleProgramBatchSlimDto> userRoleProgramBatches;

}
