package com.numpyninja.lms.util;

import com.numpyninja.lms.config.EmailConfig;
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


}
