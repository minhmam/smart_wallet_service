package com.minhpt.smart_wallet_service.email;

import com.minhpt.smart_wallet_service.util.TemplateUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerifyEmail(String to, String link) {
        try {
            String htmlTemplate = TemplateUtil.loadTemplate("templates/verify-email.html");
            String htmlContent = htmlTemplate.replace("{{VERIFY_LINK}}", link);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Verify your email");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email verify thất bại", e);
        }
    }

    @Override
    public void sendResetPasswordEmail(String to, String link) {

    }
}
