package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarAccessDeniedException extends RuntimeException {
	private static final long serialVersionUUID = 1L;
	
	public CalendarAccessDeniedException(String message) {
		super(message);
	}

}
