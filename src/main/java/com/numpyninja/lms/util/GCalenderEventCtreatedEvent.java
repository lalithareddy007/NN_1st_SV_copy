package com.numpyninja.lms.util;

import org.springframework.context.ApplicationEvent;

import com.numpyninja.lms.dto.GCalendarEventRequestDTO;

import lombok.Getter;

@Getter
public class GCalenderEventCtreatedEvent extends ApplicationEvent  {
	
	private final transient GCalendarEventRequestDTO newGCalenderEvent;

	public GCalenderEventCtreatedEvent(GCalendarEventRequestDTO newGCalenderEvent) {
		super(newGCalenderEvent);		
		this.newGCalenderEvent = newGCalenderEvent;
	}

}
