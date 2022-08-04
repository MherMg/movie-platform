package am.platform.movie.api.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * @author mher13.02.94@gmail.com
 */

@Service
public class MailSenderService {

    @Value("${spring.mail.username}")
    private String mailUsername;
    @Value("${spring.mail.password}")
    private String mailPassword;
    @Value("${spring.mail.host}")
    private String mailSmtpHost;
    @Value("${spring.mail.port}")
    private String mailSmtpPort;

    public void sendVerificationMail(String email, String code) {
        send("Verification code is", code, email);
    }


    private void send(String subject, String text, String email) {

        try {
            Session session = Session.getInstance(getMailProperties(mailUsername, mailPassword));

            MimeMessage message = new MimeMessage(session);
            message.setHeader("Content-Type", "text/plain; charset-UTF-8");
            message.setSubject(subject, "UTF-8");
            message.setText(text, "UTF-8");
            message.setFrom(new InternetAddress(mailUsername));

            InternetAddress toAddress = new InternetAddress(email);
            message.setRecipient(Message.RecipientType.TO, toAddress);
            message.setSubject(subject);
            message.setText(text);

            Transport transport = session.getTransport("smtp");
            transport.connect(mailSmtpHost, mailUsername, mailPassword);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private Properties getMailProperties(String email, String password) {
        Properties props = new Properties();
        props.put("mail.smtp.host", mailSmtpHost);
        props.put("mail.smtp.user", email);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", mailSmtpPort);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.debug", "true");
        return props;
    }

}
