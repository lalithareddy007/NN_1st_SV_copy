package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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

    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotEmpty(message = "User-Role-Program Info is mandatory")
    @JsonProperty("userRolePrograms")
    private List<UserRoleProgramSlimDto> userRolePrograms;

    @JsonProperty("status")
    private String status;
}
