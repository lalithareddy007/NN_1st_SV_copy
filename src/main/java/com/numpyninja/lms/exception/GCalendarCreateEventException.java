package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GCalendarCreateEventException extends RuntimeException{
	private static final Long serialVesionUUID=1L;
	
	public GCalendarCreateEventException(String message) {
		super(message);
	}

}
