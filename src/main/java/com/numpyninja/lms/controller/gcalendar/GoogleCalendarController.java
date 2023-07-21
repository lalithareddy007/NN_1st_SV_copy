package com.numpyninja.lms.controller.gcalendar;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.numpyninja.lms.dto.gcalendar.GCalendarEventRequestDTO;
import com.numpyninja.lms.dto.gcalendar.GCalendarEventResponseDTO;
import com.numpyninja.lms.exception.CalendarAccessDeniedException;
import com.numpyninja.lms.exception.GCalendarCreateEventException;
import com.numpyninja.lms.exception.GCalendarDeleteEventException;
import com.numpyninja.lms.exception.GCalendarIOException;
import com.numpyninja.lms.exception.GCalendarSecurityException;
import com.numpyninja.lms.services.gcalendar.GoogleCalendarService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Google Calendar Integration Controller")
public class GoogleCalendarController {

	@Autowired
	private GoogleCalendarService gCalendarService;
	
	@GetMapping(path = "/gcalendar/events", produces = "application/json")
	@ApiOperation("Gat all the events from the calendar")
	public ResponseEntity<List<GCalendarEventResponseDTO>> getCalendarEvents() throws CalendarAccessDeniedException, GCalendarIOException, GCalendarSecurityException 
	{
		//Get Calendar events
		List<GCalendarEventResponseDTO> caleResponse = gCalendarService.getEventsUsingServiceAcc();
		return new ResponseEntity<List<GCalendarEventResponseDTO>>(caleResponse, HttpStatus.OK);
	}

	@PostMapping(path = "/gcalendar/newevent", produces = "application/json")
	@ApiOperation("Gat all the calendars from given startDate to endDate")
	public ResponseEntity<String> createCalendarEvent(@Valid @RequestBody GCalendarEventRequestDTO eventRequest) 
			throws GCalendarIOException, CalendarAccessDeniedException,  GCalendarCreateEventException, GCalendarSecurityException
	{
		gCalendarService.createEventUsingServiceAcc(eventRequest);
		return new ResponseEntity<String>("Event creation success", HttpStatus.CREATED);
	}
	
	//EVent update is currently not working 
	@PutMapping(path = "/gcalendar/event/{id}", produces = "application/json")
	@ApiOperation("Gat all the calendars from given startDate to endDate")
	public ResponseEntity<String> updateCalendarEvent(@PathVariable("id")String eventId, @Valid @RequestBody GCalendarEventRequestDTO eventRequest) 
			throws GCalendarIOException, CalendarAccessDeniedException,  GCalendarCreateEventException, GCalendarSecurityException
	{
			gCalendarService.updateEvent(eventId, eventRequest);
			return new ResponseEntity<String>("Event update success", HttpStatus.OK);
	}
	
	@DeleteMapping(path = "/gcalendar/event/{id}")
	@ApiOperation("Deletes the event with the given event ID")
	public ResponseEntity<String> deleteCalendarEvent(@PathVariable ("id") String eventId) 
			throws GCalendarIOException, CalendarAccessDeniedException,  GCalendarDeleteEventException, GCalendarSecurityException
	{
			gCalendarService.deleteEvent(eventId);
			return new ResponseEntity<String>("Event deletion success", HttpStatus.OK);
	}
}
