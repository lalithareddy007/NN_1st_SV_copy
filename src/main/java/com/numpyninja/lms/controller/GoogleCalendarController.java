package com.numpyninja.lms.controller;

import java.util.List;

import javax.annotation.security.RolesAllowed;
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

import com.numpyninja.lms.dto.GCalendarEventRequestDTO;
import com.numpyninja.lms.dto.GCalendarEventResponseDTO;
import com.numpyninja.lms.exception.CalendarAccessDeniedException;
import com.numpyninja.lms.exception.GCalendarCreateEventException;
import com.numpyninja.lms.exception.GCalendarDeleteEventException;
import com.numpyninja.lms.exception.GCalendarEventNotFoundException;
import com.numpyninja.lms.exception.GCalendarIOException;
import com.numpyninja.lms.exception.GCalendarSecurityException;
import com.numpyninja.lms.services.GoogleCalendarService;

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
		List<GCalendarEventResponseDTO> caleResponse = gCalendarService.getEventsUsingServiceAcc();
		return new ResponseEntity<List<GCalendarEventResponseDTO>>(caleResponse, HttpStatus.OK);
	}

	@PostMapping(path = "/gcalendar/event", produces = "application/json")
	@ApiOperation("Gat all the calendars from given startDate to endDate")
	@RolesAllowed({"ROLE_ADMIN"})
	public ResponseEntity<GCalendarEventResponseDTO> createCalendarEvent(@Valid @RequestBody GCalendarEventRequestDTO eventRequest) 
			throws GCalendarIOException, CalendarAccessDeniedException,  GCalendarCreateEventException, GCalendarSecurityException
	{
		GCalendarEventResponseDTO event = gCalendarService.createEventUsingServiceAcc(eventRequest);
		return new ResponseEntity<GCalendarEventResponseDTO>(event, HttpStatus.CREATED);
	}
	
	@PutMapping(path = "/gcalendar/event/{id}", produces = "application/json")
	@ApiOperation("Gat all the calendars from given startDate to endDate")
	@RolesAllowed({"ROLE_ADMIN"})
	public ResponseEntity<GCalendarEventResponseDTO> updateCalendarEvent(@PathVariable("id")String eventId, @Valid @RequestBody GCalendarEventRequestDTO eventRequest) 
			throws GCalendarIOException, CalendarAccessDeniedException,  GCalendarCreateEventException, GCalendarSecurityException, GCalendarEventNotFoundException
	{
		GCalendarEventResponseDTO event = gCalendarService.updateEvent(eventId, eventRequest);
		return new ResponseEntity<GCalendarEventResponseDTO>(event, HttpStatus.OK);
	}
	
	@DeleteMapping(path = "/gcalendar/event/{id}")
	@ApiOperation("Deletes the event with the given event ID")
	@RolesAllowed({"ROLE_ADMIN"})
	public ResponseEntity<String> deleteCalendarEvent(@PathVariable ("id") String eventId) 
			throws GCalendarIOException, CalendarAccessDeniedException,  GCalendarDeleteEventException, GCalendarSecurityException
	{
		gCalendarService.deleteEvent(eventId);
		return new ResponseEntity<String>("Event deletion success", HttpStatus.OK);
	}
}
