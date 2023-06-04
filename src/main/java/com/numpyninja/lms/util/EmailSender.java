package com.numpyninja.lms.util;

import com.numpyninja.lms.entity.EmailDetails;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
public class EmailSender {

	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private Configuration freeMarkerConfigurer;

	private String emailSuccessMsg = "Email sent successfully";

//    public EmailSender(JavaMailSender javaMailSender,Configuration freeMarkerConfigurer)
//     {
//       this.javaMailSender = javaMailSender;
//       this.freeMarkerConfigurer = freeMarkerConfigurer;
//    }

	/* send simple email */
	public String sendSimpleEmail(EmailDetails emailDetails) throws MailException, IOException {
		try {
			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

			simpleMailMessage.setTo(emailDetails.getRecipient());
			simpleMailMessage.setSubject(emailDetails.getSubject());
			simpleMailMessage.setText(emailDetails.getContent());
			if (emailDetails.getCc() != null && !emailDetails.getCc().isEmpty())
				simpleMailMessage.setCc(emailDetails.getCc());

			javaMailSender.send(simpleMailMessage);
		} catch (MailException e) {
			e.printStackTrace();
			return "Email could not be sent!";
		}

		return emailSuccessMsg;
	}

	public String sendEmailUsingTemplate(EmailDetails emailDetails)
			throws MessagingException, TemplateException, IOException {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			Map<String, Object> model = emailDetails.getModel();

			Template t = freeMarkerConfigurer.getTemplate("WelcomeEmail.ftl");
			String htmlEmail = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			mimeMessageHelper.setTo(emailDetails.getRecipient());
			mimeMessageHelper.setSubject(emailDetails.getSubject());

			if (!emailDetails.getCc().isEmpty())
				mimeMessageHelper.setCc(emailDetails.getCc());

			mimeMessageHelper.setText(htmlEmail, true);

			System.out.println(javaMailSender.toString());
			javaMailSender.send(mimeMessageHelper.getMimeMessage());
		} catch (MessagingException | TemplateException | IOException e) {
			System.out.println("Exception while sending email:" + e);
			e.printStackTrace();
			return "Email could not be delivered!";
		}

		return emailSuccessMsg;
	}

	public String sendEmailUsingTemplateForgotPassword(EmailDetails emailDetails)
			throws MessagingException, TemplateException, IOException {
		try {
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			Map<String, Object> model = emailDetails.getModel();

			Template t = freeMarkerConfigurer.getTemplate("ConfirmEmail.ftl");
			String htmlEmail = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			mimeMessageHelper.setTo(emailDetails.getRecipient());
			mimeMessageHelper.setSubject(emailDetails.getSubject());

			if (!emailDetails.getCc().isEmpty())
				mimeMessageHelper.setCc(emailDetails.getCc());

			mimeMessageHelper.setText(htmlEmail, true);

			System.out.println(javaMailSender.toString());
			javaMailSender.send(mimeMessageHelper.getMimeMessage());
		} catch (MessagingException | TemplateException | IOException e) {
			System.out.println("Exception while sending email:" + e);
			e.printStackTrace();
			return "Email could not be delivered!";
		}

		return emailSuccessMsg;
	}

}
