package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GCalendarIOException extends RuntimeException {
private static final Long serialVersionUUID = 1L;
	
	public GCalendarIOException(String message) {
		super(message);
	}
}
