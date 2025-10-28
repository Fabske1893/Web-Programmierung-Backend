package com.rezeptapp.data.implemented;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    //Daten vom Versender
    private static final String EMAIL = "rezepteapp123@gmail.com";
    private static final String APP_PASSWORT = "duiw erkc vacr wkix";

    public void sendRecipeEmail(String empfaenger, String betreff, String text) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, APP_PASSWORT);
            }
        });

        try {
            Message nachricht = new MimeMessage(session);
            nachricht.setFrom(new InternetAddress(EMAIL));
            nachricht.setRecipients(Message.RecipientType.TO, InternetAddress.parse(empfaenger));
            nachricht.setSubject(betreff); 
            nachricht.setText(text);
            Transport.send(nachricht);
            System.out.println("E-Mail erfolgreich gesendet an: " + empfaenger);
            

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Fehler beim Senden der E-Mail: " + e.getMessage());
        }
    }

    
}

    
