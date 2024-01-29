package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@Builder
public class UserRoleProgramBatchMapDto {

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

        @NotNull(message = "Batch Id is mandatory")
        @DecimalMin(value = "1", message = "Batch Id must be greater than or equal to 1")
        @JsonProperty("batchId")
        private Integer batchId;

        @NotEmpty(message = "User-Role-Program-Batch Status is Mandatory")
        @Pattern(regexp = "active|inactive", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "User-Role-Program-Batch Status can be Active or Inactive")
        @JsonProperty("userRoleProgramBatchStatus")
        private String userRoleProgramBatchStatus;


}
