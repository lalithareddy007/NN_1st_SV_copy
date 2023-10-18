package com.numpyninja.lms.dto;

import java.sql.Timestamp;
import java.util.List;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.numpyninja.lms.config.ValidateStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProgramWithUsersDTO {

	private Long programId;

    @Pattern(regexp = "(^[a-zA-Z][a-zA-Z0-9 ]+$)", message = "Program Name can contain only alphabets and numbers")
    @Length(min = 4, max = 25, message = "Program Name must be of min length 4 and max length 25")

	private String programName;


	@Pattern (regexp="^[a-z0-9][a-z0-9_ ]*(?:-[a-z0-9]+)*$", message = "Program Desc can contain only alphabets and numbers")
	@Length(min = 4, max = 25, message = "Program Description must be of min length 4 and max length 25")
   	private String programDescription;

    //created custom annotation to validate status(accepts only active and inactive)
    @ValidateStatus
	private String programStatus;
    
    private List<UserDto> programUsers;

	private Timestamp creationTime;
	private Timestamp lastModTime;

}
