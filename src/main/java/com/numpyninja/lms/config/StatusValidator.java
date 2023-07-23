package com.numpyninja.lms.config;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StatusValidator implements ConstraintValidator<ValidateStatus, String>{

	@Override
	public boolean isValid(String status, ConstraintValidatorContext cxt) {
		
		List<String> statusList = Arrays.asList("active", "inactive");
        
		return statusList.contains(status.toLowerCase());		
	}

}
