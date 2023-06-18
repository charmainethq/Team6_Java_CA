package sg.edu.iss.team6.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sg.edu.iss.team6.model.Course;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String recipientEmail, String confirmationLink, String studentName, Course course) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("sa56team6@outlook.com","Course Registration");
            helper.setTo(recipientEmail);
            helper.setSubject("Confirmation Email");

            String htmlContent = "<p>Dear " + studentName + ",</p>" +
                    "<p>Please click the link below to confirm your enrollment for <br>" +
                    "Course ID: " + course.getCourseId() + " " + course.getName() +
                    "<br> Class Id </p>" +
                    "<p><a href=\"" + confirmationLink + "\">Confirm enrollment</a></p>";
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Handle exception
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}