package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GCalendarDeleteEventException extends RuntimeException {

	private final Long serialVersionUUID = 1L;
	
	public GCalendarDeleteEventException(String message) {
		super(message);
	}
}
