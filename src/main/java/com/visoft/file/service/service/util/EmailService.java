package com.visoft.file.service.service.util;


import com.visoft.file.service.Version;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;

import java.nio.charset.StandardCharsets;

import static com.visoft.file.service.service.util.PropertiesService.*;

public class EmailService {

    public static void sendError(String reportName, String email) {
        send(getEmailErrorMessage() + ": " + reportName, email);
    }

    public static void sendSuccess(String reportName, String email, Version version, String password) {
        send(
                getEmailSuccessMessage()
                + ": "
                + reportName
                + (version == Version.HE? "\nאתה יכול לראות את ההורדה שלך כאן: " : "\nYou can see your download here: ")
                + getServerName()
                + (password != null
                        ? (version == Version.HE
                            ? "\nאנא השתמש בכניסה הבאה: login " + email + " password " + password
                            : "\nPlease use next credentials: login " + email + " Password " + password)
                        : ""
                )
                , email
        );
    }

    private static void send(String emailMessage, String userEmail) {
        Email email = new SimpleEmail();
        email.setSmtpPort(587);
        email.setHostName("mail.yugan-eng.com");
        email.setAuthenticator(new DefaultAuthenticator(getEmailLogin(), getEmailPassword()));
        try {
            email.setFrom(getEmailLogin());
            email.setSubject(getEmailSubject());
            email.setMsg(new String(emailMessage.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            email.addTo(userEmail);
            email.setStartTLSEnabled(true);
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}