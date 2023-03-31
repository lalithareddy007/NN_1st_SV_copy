package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAllDto {

    @JsonProperty("user")
    private UserDto userDto;

    @JsonProperty("roles")
    private List<UserRoleMapSlimDTO> userRoleMaps;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("programBatches")
    private List<UserProgramBatchSlimDto> userProgramBatchSlimDtos;
}
