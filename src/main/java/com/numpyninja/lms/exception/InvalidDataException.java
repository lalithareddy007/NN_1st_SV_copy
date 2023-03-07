package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidDataException(String message) {
		super(message);

	}

}
