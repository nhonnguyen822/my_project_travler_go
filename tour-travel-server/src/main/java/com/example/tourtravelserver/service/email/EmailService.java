package com.example.tourtravelserver.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${app.frontend.url}")
    private String frontendUrl;
    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendUserVerificationEmail(String to, String name, String token) throws MessagingException {
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationUrl",frontendUrl + "/verification?token=" +  token);
//            context.setVariable("verificationUrl",
//                    "http://localhost:8080/api/auth/email-verification?token=" + token);

            String htmlContent = templateEngine.process("verification-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Auto Marketing System");
            helper.setTo(to);
            helper.setSubject("Yêu cầu xác nhận đăng ký tài khoản");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Lỗi encoding khi gửi email", e);
        }
    }
}
