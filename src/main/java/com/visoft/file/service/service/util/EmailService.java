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
        email.setSmtpPort(587);
        email.setHostName("mail.yugan-eng.com");
        email.setAuthenticator(new DefaultAuthenticator(getEmailLogin(), getEmailPassword()));
        try {
            email.setFrom(getEmailLogin());
            email.setSubject(getEmailSubject());
            email.setMsg(new String(emailMessage.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            email.addTo(userEmail);
            email.setStartTLSEnabled(true);
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}