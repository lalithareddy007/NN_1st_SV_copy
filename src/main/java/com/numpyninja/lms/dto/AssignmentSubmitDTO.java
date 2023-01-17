package com.numpyninja.lms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

public class AssignmentSubmitDTO {

    private Long submitID;

    @NotNull(message = "Assignment ID is mandatory")
    private Long subAssignmentID;

    @NotNull(message = "User ID is mandatory")
    private String subUserID;

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

    @DateTimeFormat(pattern="MM-dd-yyyy")
    private Timestamp subDateTime;

    private String gradedBy;

    private Timestamp gradedDateTime;

    private String grade;

    private Timestamp creationTime;

    private Timestamp lastModTime;
}
