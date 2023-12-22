package com.numpyninja.lms.util;

import com.numpyninja.lms.config.EmailConfig;
import com.numpyninja.lms.dto.EventAttendeesDTO;
import com.numpyninja.lms.dto.GCalendarEventRequestDTO;
import com.numpyninja.lms.entity.Assignment;
import com.numpyninja.lms.entity.AssignmentSubmit;
import com.numpyninja.lms.entity.UserLogin;
import com.numpyninja.lms.entity.UserRoleProgramBatchMap;
import com.numpyninja.lms.repository.UserLoginRepository;
import com.numpyninja.lms.repository.UserRoleProgramBatchMapRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    EmailConfig emailConfig;
    @Autowired
    UserRoleProgramBatchMapRepository userRoleProgramBatchMapRepository;
    @Autowired
    UserLoginRepository userLoginRepository;

    // Method to send email notification about the new assignment
    public void sendAssignmentCreatedUpdatedNotification(String userEmail, Assignment newAssignment) {

        // Customize your email content
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setSubject("New Assignment Created");
        String emailContent = generateEmailContent(userEmail, newAssignment);
        mailMessage.setText(emailContent);

        try {
            emailConfig.javaMailSender().send(mailMessage); // Send the email
            log.info("Notification sent to: " + userEmail);
            log.info("Assignment details: " + newAssignment);
        } catch (MailException e) {
           log.error(" Failed to send notification to:"  + userEmail + ". Reason:  " + e.getMessage());
        }
    }
    private String generateEmailContent(String userEmail, Assignment newAssignment) {
        // Customize the email content with assignment details and personalized information
        return "Dear " + userEmail + ", "
                + "A new assignment named '" + newAssignment.getAssignmentName() + "' has been created/updated. "
                + "Check your dashboard for more details. "
                + "Regards, "
                + "LMS Team";
    }
    @EventListener
    public void handleAssignmentCreatedUpdatedEvent(AssignmentCreatedUpdatedEvent event) {
        Assignment newAssignment = event.getNewAssignment();

        // Retrieve usersLoginMail in the batch for this assignment and send notifications
        List<UserRoleProgramBatchMap> userRoleProgramBatchMapList = userRoleProgramBatchMapRepository.findByBatch_BatchId(newAssignment.getBatch().getBatchId());
        for (UserRoleProgramBatchMap userRoleProgramBatchMap : userRoleProgramBatchMapList) {
            String userId = userRoleProgramBatchMap.getUser().getUserId();
            Optional<UserLogin> userLoginOptional = userLoginRepository.findByUserUserId(userId);
            // Check if the UserLogin object is present in the Optional
            if (userLoginOptional.isPresent()) {
                UserLogin userLogin = userLoginOptional.get();
                // Access userLoginEmail field from the UserLogin object
                String userEmail = userLogin.getUserLoginEmail();
                log.info("UserLoginEmail: " + userEmail);
                sendAssignmentCreatedUpdatedNotification(userEmail, newAssignment);
            } else {
                log.info("User login not found for the userId:" + userId);
            }
        }
    }
    public void sendAssignmentGradedNotification(String userEmail, AssignmentSubmit assignmentSubmit) {

        // Customize your email content
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setSubject("Assignment Graded");
        String fromEmail = mailMessage.getFrom();
        log.info(fromEmail);
        String emailContent = generateEmailContent1(userEmail, assignmentSubmit);
        mailMessage.setText(emailContent);

        try {
            emailConfig.javaMailSender().send(mailMessage); // Send the email
            log.info("Notification sent to: " + userEmail);
            log.info("Assignment grade details: " + assignmentSubmit);
        } catch (MailException e) {
            // Handle email sending exception
            log.error(" Failed to send notification to:"  + userEmail + ". Reason:  " + e.getMessage());
        }
    }
    private String generateEmailContent1(String userEmail, AssignmentSubmit assignmentSubmit) {
        // Customize the email content with assignment details and personalized information
        return "Dear " + userEmail + ","
                + "A new grade received for the assignment '" + assignmentSubmit.getAssignment().getAssignmentName()
                + "Check your dashboard for more details."
                + "Regards,"
                + "LMS Team";
    }
    @EventListener
    public void handleAssignmentGradeEvent (AssignmentGradedEvent gradedEvent){
        AssignmentSubmit assignmentSubmit = gradedEvent.getAssignmentSubmit();
        String userId = assignmentSubmit.getUser().getUserId();
        Optional<UserLogin> userLoginOptional = userLoginRepository.findByUserUserId(userId);
        if(userLoginOptional.isPresent()){
            UserLogin userLogin = userLoginOptional.get();
            String userEmail = userLogin.getUserLoginEmail();
            log.info("UserLoginEmail:" + userEmail);
            sendAssignmentGradedNotification(userEmail, assignmentSubmit);

        }


    }
    
    // Method to send email notification about the creation of new event in Google Calendar
    public void sendEventCreatedOnGCalendarNotification(String userEmail, GCalendarEventRequestDTO GCalendarRequestDto) {

        // Customize your email content
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(userEmail);
        mailMessage.setSubject("New Event Created on Google Calendar");
        String emailContent = generateEmailContentForGCalendar(userEmail, GCalendarRequestDto);
        mailMessage.setText(emailContent);

        try {
            emailConfig.javaMailSender().send(mailMessage); // Send the email
            log.info("Notification sent to: " + userEmail);
            log.info("Event details: " + GCalendarRequestDto);
        } catch (MailException e) {
           log.error(" Failed to send notification to:"  + userEmail + ". Reason:  " + e.getMessage());
        }
    }
    private String generateEmailContentForGCalendar(String userEmail, GCalendarEventRequestDTO GCalendarRequestDto) {
        // Customize the email content with Google calendar Event details
        return "Dear " + userEmail + ", "
                + "A new Google Calendar Event named '" + GCalendarRequestDto.getEventDescription() + "' has been created. "
                + "When : "+ GCalendarRequestDto.getEventStartDateTime()
                + " Where : "+ GCalendarRequestDto.getLocation()
                + " Zoom Link : " + GCalendarRequestDto.getAttachments().get(0).getFileUrl()
                + "  Check your dashboard for more details. "
                + " Regards, "
                + "LMS Team";
    }
    
    @EventListener
	public void handleGCalenderEventCteatedEvent(GCalenderEventCtreatedEvent newGCalendarEventCreatedEvent) {
		
		GCalendarEventRequestDTO GCalendarRequestDto = newGCalendarEventCreatedEvent.getNewGCalenderEvent();
		
	        // Retrieve usersLoginMail in the batch for this assignment and send notifications
		List<EventAttendeesDTO> attendeesList = GCalendarRequestDto.getAttendees();
		
		for(EventAttendeesDTO attendee : attendeesList) {
			String attendeeEmail = attendee.getEmail();
			log.info("UserLoginEmail:" + attendeeEmail);
			sendEventCreatedOnGCalendarNotification(attendeeEmail , GCalendarRequestDto);
		}
	}


}
