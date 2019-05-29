package com.visoft.file.service.service.util;


import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import java.nio.charset.StandardCharsets;

import static com.visoft.file.service.service.util.PropertiesService.*;

public class EmailService {

    public static void sendError(String reportName, String email) {
        send(getEmailErrorMessage() + " " + reportName, email);
    }

    public static void sendSuccess(String reportName, String email) {
        send(getEmailSuccessMessage() + " " + reportName, email);
    }

    private static void send(String emailMessage, String userEmail) {
        Email email = new SimpleEmail();
        email.setHostName("smtp.googlemail.com");
        email.setSmtpPort(465);
        email.setAuthenticator(new DefaultAuthenticator(getEmailLogin(), getEmailPassword()));
        email.setSSLOnConnect(true);
        try {
            email.setFrom(getEmailLogin());
            email.setSubject(getEmailSubject());
            email.setMsg(new String(emailMessage.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            email.addTo(getEmailTo());
            email.addTo(userEmail);
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}