package com.numpyninja.lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix="mail")
public class EmailConfigure {
    //@Value("${spring.mail.host}")
    private String host;
    //@Value("${spring.mail.port}")
    private int port;
    //@Value("${spring.mail.username}")
    private String userName;
    //@Value("${spring.mail.password}")
    private String password;
    //@Value("${spring.mail.host}")
    private String smtpServer;

//    @Value("${mail.transfer.protocol}")
//    private String protocol;

//    @Value("${mail.smtp.starttls.enable}")
//    private String starttls;
//
//    @Value("${mail.smtp.auth}")
//    private String auth;

    @Bean
    public JavaMailSender getJavaMailSender(){
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
        return javaMailSender;
    }
}
