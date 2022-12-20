package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	String resourceName;
	String fieldName;
	long fieldValue;

	public InvalidDataException(String resourceName, String fieldName, long fieldValue) {
		super(String.format("%s already exists with given %s : %s ",resourceName, fieldName, fieldValue));
		this.resourceName = resourceName;
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

	public InvalidDataException(String message) {
		super(message);
		
	}

}
