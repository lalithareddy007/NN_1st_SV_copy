package com.numpyninja.lms.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.numpyninja.lms.config.WithMockAdmin;
import com.numpyninja.lms.dto.EventAttendeesDTO;
import com.numpyninja.lms.dto.GCalendarEventRequestDTO;
import com.numpyninja.lms.dto.GCalendarEventResponseDTO;
import com.numpyninja.lms.services.GoogleCalendarService;

import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(GoogleCalendarController.class)
@WithMockUser
public class GoogleCalendarControllerTest extends AbstractTestController {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GoogleCalendarService calendarService;

	@Autowired
	private ObjectMapper objectMapper;

	private GCalendarEventResponseDTO mockEventResponse;
	private GCalendarEventRequestDTO mockEventRequest;

	@BeforeEach
	public void setup() {
		mockEventRequest = GCalendarEventRequestDTO.builder()
				.attendees(Arrays.asList(new EventAttendeesDTO("gkav2022@gmail.com"))).attachments(null)
				.eventSummary("This is first test event created from LMS")
				.eventDescription("This is first test event created from LMS")
				.eventStartDateTime("2023-20-07T08:00:00-04:00").eventEndDateTime("2023-20-07T09:00:00-04:00").build();

		mockEventResponse = GCalendarEventResponseDTO.builder().eventId("1")
				.eventSummary("This is first test event created from LMS")
				.eventDescription("This is first test event created from LMS")
				.eventStartDateTime("2023-20-07T08:00:00-04:00").eventEndDateTime("2023-20-07T09:00:00-04:00")
				.eventStatus("confirmed").location("zoom call").build();
	}

	@Test
	@SneakyThrows
	@DisplayName("GCalendarTest: GetCalendarEvents")
	void getAllEvents() {
		when(calendarService.getEventsUsingServiceAcc()).thenReturn(Arrays.asList(mockEventResponse));
		
		ResultActions resultActions = mockMvc.perform(get("/gcalendar/events"));
		
		resultActions.andExpectAll(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				//.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(mockEventResponse)))
				.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].eventSummary", equalTo(mockEventResponse.getEventSummary())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].eventDescription", equalTo(mockEventResponse.getEventDescription())))
		
		
		
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].eventStartDateTime", equalTo(mockEventResponse.getEventStartDateTime())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].eventEndDateTime", equalTo(mockEventResponse.getEventEndDateTime())))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].eventStatus", equalTo(mockEventResponse.getEventStatus())));
				
				verify(calendarService).getEventsUsingServiceAcc();
		
	}

	@Test
	@SneakyThrows
	@DisplayName("GCalendarTest: CreateCalendarEvent")
	@WithMockAdmin
	void createCalendarEvent() {
		
		 when(calendarService.createEventUsingServiceAcc(Mockito.any(GCalendarEventRequestDTO.class))).thenReturn(mockEventResponse);
		 ResultActions resultActions = mockMvc.perform(post("/gcalendar/event")
				 .contentType(MediaType.APPLICATION_JSON)
				 .content(objectMapper.writeValueAsString(mockEventRequest)));
		 
		 resultActions.andExpect(status().isCreated())
			.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(mockEventResponse)))
			.andExpect(MockMvcResultMatchers.jsonPath("$.eventSummary", equalTo(mockEventResponse.getEventSummary())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.eventDescription", equalTo(mockEventResponse.getEventDescription())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.eventStartDateTime", equalTo(mockEventResponse.getEventStartDateTime())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.eventEndDateTime", equalTo(mockEventResponse.getEventEndDateTime())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.eventStatus", equalTo(mockEventResponse.getEventStatus())));
		 verify(calendarService).createEventUsingServiceAcc(Mockito.any(GCalendarEventRequestDTO.class));
	}

	@Test
	@SneakyThrows
	@DisplayName("GCalendarTest: DeleteCalendarEvent")
	@WithMockAdmin
	void deleteCalendarEvent() {
		String eventId = "1";
		when(calendarService.deleteEvent(Mockito.any(String.class))).thenReturn(true);
		ResultActions resultActions = mockMvc.perform(delete("/gcalendar/event/" + eventId));
		resultActions.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("Event deletion success"));
	}

	@Test
	@SneakyThrows
	@DisplayName("GCalendarTest: UpdateCalendarEvent")
	@WithMockAdmin
	void updateCalendarEvent() {
		String eventId = "1";
		when(calendarService.updateEvent(Mockito.any(String.class), Mockito.any(GCalendarEventRequestDTO.class)))
				.thenReturn(mockEventResponse);
		ResultActions resultActions = mockMvc.perform(put("/gcalendar/event/" + eventId)
				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(mockEventRequest)));

		resultActions.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType("application/json"))
				.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(mockEventResponse)))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.eventSummary", equalTo(mockEventResponse.getEventSummary())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.eventDescription",
						equalTo(mockEventResponse.getEventDescription())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.eventStartDateTime",
						equalTo(mockEventResponse.getEventStartDateTime())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.eventEndDateTime",
						equalTo(mockEventResponse.getEventEndDateTime())))
				.andExpect(
						MockMvcResultMatchers.jsonPath("$.eventStatus", equalTo(mockEventResponse.getEventStatus())));

		verify(calendarService).updateEvent(Mockito.any(String.class), Mockito.any(GCalendarEventRequestDTO.class));

	}
}
