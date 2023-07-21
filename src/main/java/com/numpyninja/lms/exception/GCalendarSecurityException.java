package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GCalendarSecurityException extends Exception {

	private static final Long serialVesionUUID = 1L;
	
	public GCalendarSecurityException(String message) {
		super(message);
	}
}
