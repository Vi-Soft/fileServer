package com.visoft.file.service.service.util;


import com.visoft.file.service.Version;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;

import java.nio.charset.StandardCharsets;

import static com.visoft.file.service.service.util.PropertiesService.getEmailErrorMessage;
import static com.visoft.file.service.service.util.PropertiesService.getEmailLogin;
import static com.visoft.file.service.service.util.PropertiesService.getEmailPassword;
import static com.visoft.file.service.service.util.PropertiesService.getEmailSubject;
import static com.visoft.file.service.service.util.PropertiesService.getServerName;

public class EmailService {

    private static final String MAIL_TEMPLATE = "<html> <head> <meta charset=\"UTF-8\"> </head> <body> %s </body> </html>";

    public static void sendError(String reportName, String email) {
        send(getEmailErrorMessage() + ": " + reportName, email);
    }

    public static void sendSuccess(String reportName, String email, Version version, boolean isWinMode, String password) {
        sendHtml(getSuccessMessage(reportName, email, version, isWinMode, password), email, version);
    }

    public static void sendSharedSuccess(String reportName, String email, String sharedEmail, Version version, String password) {
        sendHtml(getSharedSuccessMessage(reportName, email, sharedEmail, version, password), sharedEmail, version);
    }

    private static String getSharedSuccessMessage(String reportName, String email, String sharedEmail, Version version, String password) {
        return  (version == Version.IL ? "שלום רב," : "Greetings,") + "<br><br>"
            + email + (version == Version.IL ? " שיתף אותך בקובץ נקודת עצירה." : " has shared a Stage Gate form with you.") + "<br><br>"
            + (version == Version.IL ? "שם הקובץ " : "The file name is ") + reportName
            + (version == Version.IL ? " ניתן להכנס למערכת ההורדות בקישור הבא:" : " and accessible with the following link:") + "<br>"
            + getServerName() + "<br><br>"
            + (version == Version.IL ? "שם משתמש: " : "Username: ") + sharedEmail + "<br>"
            + (version == Version.IL ? "סיסמא: " : "Password: ") + password + "<br><br>"
            + (version == Version.IL ? "שימו ,לב הורדת הפרוייקט תהיה זמינה שבוע לכל הפחות ולאחר מכן ההורדה תימחק מהשרת." : "Attention, the downloaded material will be available for at least 1 week, afterward it will be deleted from the server.") + "<br><br>"
            + (version == Version.IL ? "לעזרה ופרטים נוספים ניתן לפנות אל התמיכה באמצעות:" : "For assistance and further information, you can contact support at:") + "<br>"
            + "support@visoft-eng.com<br>"
            + "074-7419412<br><br>"
            + (version == Version.IL ? "בברכה," : "Best regards,") + "<br>"
            + (version == Version.IL ? "צוות וי-סופט." : "Vi-Soft team.") + "<br><br>"
            + (version == Version.IL ? "הודעה זו נשלחה באופן אוטומטי - אין להגיב להודעה זו." : "This message was sent automatically - please do not reply to this message.") + "<br><br>";
    }

    private static String getSuccessMessage(String reportName, String email, Version version, boolean isWinMode, String password) {
        String message = (version == Version.IL ? "שלום רב," : "Greetings,") + "<br>"
            + (version == Version.IL ? "הורדת הפרוייקט הסתיימה בהצלחה." : "The project download has successfully finished.") + "<br>"
            + (version == Version.IL ? "שם התיקיה: " : "Folder name: ") + reportName + "<br><br>"
            + (version == Version.IL ? "ניתן להכנס למערכת ההורדות בקישור הבא:" : "The download system is accessible with the following link:") + "<br>"
            + getServerName() + "<br><br>"
            + (version == Version.IL ? "שם משתמש: " : "Username: ") + email + "<br>"
            + (version == Version.IL ? "סיסמא: " : "Password: ") + password + "<br><br>"
            + (version == Version.IL ? "הצעדים הדרושים:" : "Following steps:") + "<br><br>"
            + (version == Version.IL ? "1. במסך יופיעו כל הפרויקטים הניתנים להורדה. בוחרים פרויקט מסוים." : "1. All downloadable projects will appear on the screen. Choose a specific project.") + "<br>"
            + (version == Version.IL ? "2. לוחצים על כפתור ההורדה" : "2. Press the download button.") + "<br>"
            + (version == Version.IL ? "3. התיקייה שיורדת היא בפורמט ZIP." : "3. The downloaded folder is in ZIP format.") + "<br>"
            + (version == Version.IL ? "4. יש לחלץ את הקבצים לתיקיה רגילה (מומלץ להשתמש בתוכנה 7zip)." : "4. Extract the files to a standard folder (we recommend using 7zip).") + "<br>";

        if (isWinMode) {
            message += (version == Version.IL ? "5. כל הקבצים יופיעו בתיקיות ומסודרים לפי עץ הפרוייקט." : "5. All the files are exported to folders arranged by the project tree.") + "<br><br>";
        } else {
            message +=
                (version == Version.IL ? "5. בתיקייה שנוצרה יש ללחוץ על קובץ  DOUBLE-CLICK-ME" : "5. In the extracted folder, click on the DOUBLE-CLICK-ME file.") + "<br>"
                + (version == Version.IL ? "6. הקובץ נפתח בדפדפן ובתפריט שנפתח אפשר למצוא את הקבצים הדרושים לפי מיקומם בעץ הפרויקט." : "6. The file is opened in the browser and you can find the necessary files in the menu according to their location in the project tree.") + "<br><br>";
        }

        return message
            + (version == Version.IL ? "שימו ,לב הורדת הפרוייקט תהיה זמינה שבוע לכל הפחות ולאחר מכן ההורדה תימחק מהשרת." : "Attention, the downloaded material will be available for at least 1 week, afterward it will be deleted from the server.") + "<br><br>"
            + (version == Version.IL ? "בברכה," : "Best regards,") + "<br>"
            + (version == Version.IL ? "צוות וי-סופט." : "Vi-Soft team.");
    }

    public static void sendShared(String reportName, String senderEmail, String email, String password) {
        send(
            "שלום רב," + "\n"
                + "שיתף אותך בהורדת מסמכי .הפרוייקט " + senderEmail + "\n"
                + "שם התיקיה: " + reportName + "\n\n"
                + "קישור למערכת ההורדה:" + "\n\n"
                + getServerName() + "\n\n"
                + "שם משתמש " + email + "\n"
                + "סיסמא " + password + "\n\n"
                + "הצעדים הדרושים:" + "\n\n"
                + "1. במסך יופיעו כל הפרויקטים הניתנים להורדה. בוחרים פרויקט מסוים." + "\n"
                + "2. לוחצים על כפתור ההורדה" + "\n"
                + "3. התיקייה שיורדת היא בפורמט ZIP." + "\n"
                + "4. יש לחלץ את הקבצים לתיקיה רגילה." + "\n"
                + "5. בתיקייה שנוצרה יש ללחוץ על קובץ DOUBLE-CLICK-ME." + "\n"
                + "6. הקובץ נפתח בדפדפן ובתפריט שנפתח אפשר למצוא את הקבצים הדרושים לפי מיקומם בעץ הפרויקט." + "\n\n"
                + "שימו ,לב הורדת הפרוייקט תהיה זמינה שבוע לכל הפחות ולאחר מכן ההורדה תימחק .מהשרת" + "\n\n"
                + "בברכה," + "\n"
                + "צוות Vi-Soft." + "\n\n"
                + "Greetings,\n"
                + senderEmail + " has shared with you a project download.\n"
                + "Folder name: " + reportName + "\n\n"
                + "Link to the download system:\n\n"
                + getServerName() + "\n\n"
                + "Username " + email + "\n"
                + "Password  " + password + "\n\n"
                + "Necessary steps:\n\n"
                + "1. All downloadable projects will appear on the screen. Choose a specific project.\n"
                + "2. Press the download button.\n"
                + "3. The downloaded folder is in ZIP format.\n"
                + "4. Extract the files to a standard folder.\n"
                + "5. In the extracted folder, click on the DOUBLE-CLICK-ME file.\n"
                + "6. The file is opened in the browser and you can find the necessary files in the menu according to their location in the project tree.\n\n"
                + "Attention, the downloaded material will be available for at least 1 week, afterward it will be deleted from the server.\n\n"
                + "Best regards,\n"
                + "Vi-Soft team.\n"
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

    private static void sendHtml(String emailMessage, String userEmail, Version version) {
        HtmlEmail email = new HtmlEmail();
        email.setSmtpPort(587);
        email.setHostName("mail.yugan-eng.com");
        email.setAuthenticator(new DefaultAuthenticator(getEmailLogin(), getEmailPassword()));
        try {
            email.setFrom(getEmailLogin());
            email.setSubject(getEmailSubject());
            email.setCharset("utf-8");
            email.setHtmlMsg(
                String.format(
                    MAIL_TEMPLATE,
                    "<div style='direction:" + (version == Version.IL ? "rtl" : "ltr") + "'>"
                        + emailMessage
                        + "</div>"
                )
            );
            email.addTo(userEmail);
            email.setStartTLSEnabled(true);
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
