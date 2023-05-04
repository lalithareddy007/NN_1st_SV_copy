package com.numpyninja.lms.util;

import com.numpyninja.lms.entity.EmailDetails;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class EmailSender{

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private Configuration freeMarkerConfig;

//    @Value("${spring.mail.username}")
//    private String senderEmail;

    private String emailSuccessMsg = "Email sent successfully";

//    public EmailSender(JavaMailSender javaMailSender){
//       this.javaMailSender = javaMailSender;
//       //this.freeMarkerConfiguration = freeMarkerConfiguration;
//    }

    /*send simple email */
    public String sendSimpleEmail(EmailDetails emailDetails) throws MailException, IOException {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(emailDetails.getRecipient());
            simpleMailMessage.setSubject(emailDetails.getSubject());
            simpleMailMessage.setText(emailDetails.getContent());
            if (emailDetails.getCc()!=null && !emailDetails.getCc().isEmpty())
                simpleMailMessage.setCc(emailDetails.getCc());

            javaMailSender.send(simpleMailMessage);
        }
        catch(MailException e){
           e.printStackTrace();
            return "Email could not be sent!";
        }

        return emailSuccessMsg;
    }

    public String sendSimpleEmailWithFreeTemplate(EmailDetails emailDetails) throws MessagingException, TemplateException,
                                                    IOException{
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            Map<String,Object> model = emailDetails.getModel();

            Template t = freeMarkerConfig.getTemplate("WelcomeEmail.ftl");
            String htmlEmail = FreeMarkerTemplateUtils.processTemplateIntoString(t,model);

            //SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            //simpleMailMessage.setFrom(senderEmail);
            mimeMessageHelper.setTo(emailDetails.getRecipient());
            mimeMessageHelper.setSubject(emailDetails.getSubject());
            //mimeMessageHelper.setText(emailDetails.getBody());
            if (!emailDetails.getCc().isEmpty())
                mimeMessageHelper.setCc(emailDetails.getCc());

            //emailDetails.setContent(getContentFromTemplate(emailDetails.getModel()));
            mimeMessageHelper.setText(htmlEmail,true);

            System.out.println(javaMailSender.toString());
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
        }
        catch(MessagingException | TemplateException | IOException e){
            System.out.println("Exception while sending email:"+e);
            e.printStackTrace();
            return "Email could not be delivered!";
        }

        return emailSuccessMsg;
    }

//    private String getContentFromTemplate(Map<String,Object> model){
//        StringBuffer content= new StringBuffer();
//        try{
//            FreeMarkerTemplateUtils.processTemplateIntoString();
//            content.append();
//        }
//        catch (Exception e){
//            System.out.println("Exception while reading Email template file reading...");
//            e.getStackTrace();
//        }
//        return content;
//
//    }

}
