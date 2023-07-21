package com.numpyninja.lms.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GCalendarEventNotFoundException extends RuntimeException {

	private final Long serialVersionUUID = 1L;
	private String eventId;
	
	public GCalendarEventNotFoundException(String eventId) {
		super("Calendar event with id:" + eventId + " not found");
	}
}
