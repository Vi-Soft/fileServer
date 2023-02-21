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
            (version == Version.IL? "שלום רב," : "Greetings,") + "\n"
                + (version == Version.IL? "הורדת הפרוייקט הסתיימה .בהצלחה" : "The project download has successfully finished.") + "\n"
                + (version == Version.IL? "שם התיקיה: " : "Folder name: ") + reportName + "\n\n"
                + (version == Version.IL? "קישור למערכת ההורדה:" : "Link to the download system:") + "\n\n"
                + getServerName() + "\n\n"
                + (version == Version.IL? "שם משתמש " : "Username ") + email + "\n"
                + (version == Version.IL? "סיסמא " : "Password  ") + password + "\n\n"
                + (version == Version.IL? "הצעדים הדרושים:" : "Necessary steps:") + "\n\n"
                + (version == Version.IL? "1. במסך יופיעו כל הפרויקטים הניתנים להורדה. בוחרים פרויקט מסוים." : "1. All downloadable projects will appear on the screen. Choose a specific project.") + "\n"
                + (version == Version.IL? "2. לוחצים על כפתור ההורדה" : "2. Press the download button.") + "\n"
                + (version == Version.IL? "3. התיקייה שיורדת היא בפורמט ZIP." : "3. The downloaded folder is in ZIP format.") + "\n"
                + (version == Version.IL? "4. יש לחלץ את הקבצים לתיקיה רגילה." : "4. Extract the files to a standard folder.") + "\n"
                + (version == Version.IL? "5. בתיקייה שנוצרה יש ללחוץ על קובץ RUN_ME." : "5. In the extracted folder, click on the RUN_ME file.") + "\n"
                + (version == Version.IL? "6. הקובץ נפתח בדפדפן ובתפריט שנפתח אפשר למצוא את הקבצים הדרושים לפי מיקומם בעץ הפרויקט." : "6. The file is opened in the browser and you can find the necessary files in the menu according to their location in the project tree.") + "\n\n"
                + (version == Version.IL? "שימו ,לב הורדת הפרוייקט תהיה זמינה שבוע לכל הפחות ולאחר מכן ההורדה תימחק .מהשרת" : "Attention, the downloaded material will be available for at least 1 week, afterward it will be deleted from the server.") + "\n\n"
                + (version == Version.IL? "בברכה," : "Best regards,") + "\n"
                + (version == Version.IL? "צוות וי-סופט." : "Vi-Soft team.") + "\n"
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
