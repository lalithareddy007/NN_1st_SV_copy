package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentSubmitDTO {

    private Long submissionId;

    @NotNull(message = "Assignment ID is mandatory")
    private Long assignmentId;

    @NotEmpty(message = "User ID is mandatory")
    private String userId;

    /*This should be same as the assignment name and need to come from the front-end.
    * This shouldn't be editable, or it would be difficult for the user
    * to identify which assignment he/she has submitted. */
    @NotNull(message = "Submission description is mandatory")
    private String subDesc;

    private String subComments;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subPathAttach1;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subPathAttach2;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subPathAttach3;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subPathAttach4;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subPathAttach5;

    //@DateTimeFormat(pattern="MM-dd-yyyy")
    private Timestamp subDateTime;

    private String gradedBy;

    private Timestamp gradedDateTime;

    private int grade;

    private Timestamp creationTime;

    private Timestamp lastModTime;
}
