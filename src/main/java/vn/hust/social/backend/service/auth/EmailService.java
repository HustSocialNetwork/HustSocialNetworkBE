package vn.hust.social.backend.service.auth;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.email}")
    private String from;

    public void sendVerificationEmail(String to, String name, String verifyLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("verifyLink", verifyLink);

        String htmlContent = templateEngine.process("verify-email.html", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject("Verify your email - Muniversity");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}

