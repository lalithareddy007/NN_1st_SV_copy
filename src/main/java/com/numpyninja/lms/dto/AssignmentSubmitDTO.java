package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;

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

    @JsonFormat(pattern="MM-dd-yyyy HH:mm:ss")
    private Timestamp subDateTime;

    private String gradedBy;

    @JsonFormat(pattern="MM-dd-yyyy")
    private Timestamp gradedDateTime;

    /*Defaulting the grade value to -1 inorder to not save 0 as value
    in db during submission creation. Front-end needs to diplay 'grade' if its value is
    greater than the default value.
     */
    private int grade =-1;

   private Timestamp creationTime;

   private Timestamp lastModTime;
}
