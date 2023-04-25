package com.numpyninja.lms.util;

import com.numpyninja.lms.entity.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class EmailSender{

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private String emailSuccessMsg = "Email sent successfully";

   // public EmailSender(JavaMailSender javaMailSender){
//        this.javaMailSender = javaMailSender;
//    }

    /*send simple email */
    public String sendSimpleEmail(EmailDetails emailDetails){
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(emailDetails.getRecipient());
            simpleMailMessage.setSubject(emailDetails.getSubject());
            simpleMailMessage.setText(emailDetails.getBody());
            if (!emailDetails.getCc().isEmpty())
                simpleMailMessage.setCc(emailDetails.getCc());

            javaMailSender.send(simpleMailMessage);
        }
        catch(Exception e){
            System.out.println("Exception while sending email:"+e);
            return "Email could not be sent!";
        }

        return emailSuccessMsg;
    }

    public String sendSimpleEmailWithUniqueLink(EmailDetails emailDetails){
        try {

            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(emailDetails.getRecipient());
            simpleMailMessage.setSubject(emailDetails.getSubject());
            simpleMailMessage.setText(emailDetails.getBody());
            if (!emailDetails.getCc().isEmpty())
                simpleMailMessage.setCc(emailDetails.getCc());

            javaMailSender.send(simpleMailMessage);
        }
        catch(Exception e){
            System.out.println("Exception while sending email:"+e);
            return "Email could not be sent!";
        }

        return emailSuccessMsg;
    }

}
