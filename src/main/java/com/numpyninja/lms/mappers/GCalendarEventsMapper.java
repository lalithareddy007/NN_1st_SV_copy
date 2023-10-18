package com.numpyninja.lms.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttachment;
import com.google.api.services.calendar.model.EventAttendee;
import com.numpyninja.lms.dto.EventAttachmentDTO;
import com.numpyninja.lms.dto.EventAttendeesDTO;
import com.numpyninja.lms.dto.GCalendarEventResponseDTO;



@Component
public final class GCalendarEventsMapper {
	
   public static GCalendarEventResponseDTO mapToGCalendarEventResponseDTO(Event event) {
    	GCalendarEventResponseDTO gcEventResponseDTO = new GCalendarEventResponseDTO();
    	gcEventResponseDTO.setEventId(event.getId());
    	gcEventResponseDTO.setEventDescription(event.getDescription());
    	gcEventResponseDTO.setEventSummary(event.getSummary());
    	gcEventResponseDTO.setEventStatus(event.getStatus());
    	gcEventResponseDTO.setLocation(event.getLocation());
    	gcEventResponseDTO.setEventStartDateTime(event.getStart().getDateTime().toString());
    	gcEventResponseDTO.setEventEndDateTime(event.getEnd().getDateTime().toString());
    	return gcEventResponseDTO;
    }
   public static List<GCalendarEventResponseDTO> mapToGCalendarEventResponseDTOList(List<Event> events) {
    	List<GCalendarEventResponseDTO> gcEventResponseDTOList = new ArrayList<>();
    	gcEventResponseDTOList = events.stream()
    			.map(event-> new GCalendarEventResponseDTO(
    			event.getId(), 
    			event.getDescription(), 
    			event.getSummary(), 
    			event.getStatus(), 
    			event.getLocation(), 
    			event.getStart().getDateTime().toString(),
    			event.getEnd().getDateTime().toString()))
    			.collect(Collectors.toList());
    	return gcEventResponseDTOList;
   }
   
   public static List<EventAttachmentDTO> mapToEventAttachmentDTO (List<EventAttachment> gCalEventAttachement) {
	   
	   return gCalEventAttachement.stream()
			   .map(attachment -> new EventAttachmentDTO(attachment.getFileUrl(), attachment.getMimeType(), attachment.getTitle()))
			   .collect(Collectors.toList());
   }
   
   public static List<EventAttachment>  mapToEventAttachment (List<EventAttachmentDTO> eventAttachmentDtoList) {
	   return eventAttachmentDtoList.stream()
			   .map(attachment-> new EventAttachment()
					   .setFileUrl(attachment.getFileUrl())
					   .setMimeType(attachment.getMimeType())
					   .setTitle(attachment.getTitle()))
			   .collect(Collectors.toList());
   }
   
   public static List<EventAttendeesDTO> mapToEventAttendeesDTO (List<EventAttendee> gCalEventAttendees) {
	   return gCalEventAttendees.stream()
			   .map(attendee-> new EventAttendeesDTO(attendee.getEmail()))
			   .collect(Collectors.toList());
   }
   
   public static List<EventAttendee> mapToEventAttendees (List<EventAttendeesDTO> eventAttendeesDtoList) {
	   return eventAttendeesDtoList.stream()
			   .map(attendee-> new EventAttendee()
					   .setEmail(attendee.getEmail()))
			   .collect(Collectors.toList());
   }
}
