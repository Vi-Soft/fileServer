package com.visoft.file.service.service.util;


import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import java.nio.charset.StandardCharsets;

import static com.visoft.file.service.service.util.PropertiesService.*;

public class EmailService {

    public static void sendError(String reportName) {
        send(getEmailErrorMessage() + " " + reportName);
    }

    public static void sendSuccess(String reportName) {
        send(getEmailSuccessMessage() + " " + reportName);
    }

    private static void send(String emailMessage) {
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
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}