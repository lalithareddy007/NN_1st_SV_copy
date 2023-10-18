package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GCalendarGetEventsException extends RuntimeException{

	private static final Long serialVersionUUID = 1L;
	
	public GCalendarGetEventsException(String message) {
		super(message);
	}
}
