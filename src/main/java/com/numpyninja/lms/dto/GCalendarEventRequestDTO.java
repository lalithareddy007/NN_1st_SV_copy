package com.numpyninja.lms.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GCalendarEventRequestDTO {
	
	@NotBlank(message = "Event description cannot be empty")
	private String eventDescription;
	
	@NotBlank(message = "Event title cannot be empty")
	private String eventSummary;
	
	private String location;
	
	@NotBlank(message = "Event start date time cannot be empty, example format- 2023-07-10T13:00:00-04:00")
	private String eventStartDateTime;
	
	@NotBlank(message ="Event end date time cannot be empty, example format- 2023-07-10T13:00:00-04:00")
	private String eventEndDateTime;
	
	private List<EventAttendeesDTO> attendees;
	
	private List<EventAttachmentDTO> attachments;

}