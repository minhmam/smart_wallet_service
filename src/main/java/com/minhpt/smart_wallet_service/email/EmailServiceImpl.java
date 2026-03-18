package com.minhpt.smart_wallet_service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender mailSender;

    @Override
    public void sendVerifyEmail(String to, String link) {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setSubject("Verify your email");
        mail.setText("Click link: " + link);

        mailSender.send(mail);
    }

    @Override
    public void sendResetPasswordEmail(String to, String link) {

    }
}
