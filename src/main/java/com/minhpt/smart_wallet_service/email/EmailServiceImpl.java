package com.minhpt.smart_wallet_service.email;

import com.minhpt.smart_wallet_service.i18n.MessageResolver;
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
    private final MessageResolver messageResolver;

    @Override
    public void sendVerifyEmail(String to, String link) {
        try {
            String htmlTemplate = TemplateUtil.loadLocalizedTemplate(
                    "templates/verify-email.html",
                    messageResolver.getCurrentLocale()
            );
            String htmlContent = htmlTemplate.replace("{{VERIFY_LINK}}", link);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(messageResolver.get("email.verify.subject"));
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email verify thất bại", e);
        }
    }

    @Override
    public void sendResetPasswordEmail(String to, String newPassword) {
        try {
            String htmlTemplate = TemplateUtil.loadLocalizedTemplate(
                    "templates/reset-password.html",
                    messageResolver.getCurrentLocale()
            );
            String htmlContent = htmlTemplate.replace("{{NEW_PASSWORD}}", newPassword);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(messageResolver.get("email.reset_password.subject"));
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email reset mật khẩu thất bại", e);
        }
    }
}
