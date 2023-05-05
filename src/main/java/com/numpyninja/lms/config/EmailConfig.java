package com.numpyninja.lms.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${mail.host}")
    private String host;
    @Value("${mail.port}")
    private int port;
    @Value("${mail.username}")
    private String userName;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.protocol}")
    private String protocol;

//    @Value("${mail.protocol}")
//    private String protocol;

//    @Value("${mail.smtp.starttls.enable}")
//    private String starttls;
//
//    @Value("${mail.smtp.auth}")
//    private String auth;

    @Bean
    public JavaMailSender getJavaMailSender(){
        System.out.println("Mail property values:"+host+" "+port+" "+userName+" "+password);
        JavaMailSenderImpl javaMailSender =  new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(userName);
        javaMailSender.setPassword(password);
        Properties prop = javaMailSender.getJavaMailProperties();
        //prop.put("mail.transfer.protocol",protocol);

        prop.put("mail.debug","true");
        prop.put("mail.smtp.starttls.enable","true");
        prop.put("mail.smtp.auth","true");
        javaMailSender.setJavaMailProperties(prop);
        return javaMailSender;
    }

    @Bean
    public FreeMarkerConfigurer getFreeMarkerConfigurer(){
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath("classpath:/templates/email-templates/");
        return freeMarkerConfigurer;
    }

}
