package com.rezeptapp.data.implemented;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    //Daten vom Versender
    private static final String EMAIL = "";
    private static final String APP_PASSWORT = "";

    public void sendConfirmationEmail(String empfaenger) {
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
            nachricht.setSubject("Bestätigung deines Rezepts");
            nachricht.setText("Glückwunsch! Dein Rezept wurde erfolgreich erstellt.\n\n Wir wünschen dir weiterhin viel Spaß mit unseren Rezepten!");
            Transport.send(nachricht);
            

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // Dbeispielhafte main Methode
    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        
        
        String empfaengerEmail = "";
        
        

        emailService.sendConfirmationEmail(empfaengerEmail);
    }
}

    
