package com.numpyninja.lms.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = StatusValidator.class)
public @interface ValidateStatus {
	
	 public String message() default "Invalid Status: must be Active or Inactive";
	   
	 Class<?>[] groups() default { };

	 Class<? extends Payload>[] payload() default { };

}
