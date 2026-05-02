package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.email.EmailService;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.model.VerificationToken;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.repository.VerificationTokenRepository;
import com.minhpt.smart_wallet_service.service.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${link.baseUrl}")
    private String baseUrl;

    @Value("${link.webUrl}")
    private String webUrl;

    @Override
    public void sendVerifyEmail(User user) {

        String token = UUID.randomUUID().toString();

        VerificationToken vt = VerificationToken.builder()
                .token(token)
                .expiredTime(LocalDateTime.now().plusHours(24))
                .used(Constant.NOT_USED)
                .user(user)
                .build();

        verificationTokenRepository.save(vt);

        String link = webUrl + "/auth/verify?token=" + token;

        emailService.sendVerifyEmail(user.getEmail(), link);
    }

    @Override
    public void verifyEmail(String token) {

        VerificationToken vt = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (vt.getUsed() == Constant.USED)
            throw new RuntimeException("Token already used");

        if (vt.getExpiredTime().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Token expired");

        User user = vt.getUser();

        user.setVerified(Constant.VERIFIED);

        userRepository.save(user);

        vt.setUsed(Constant.USED);
        verificationTokenRepository.save(vt);
    }
}
