package com.minhpt.smart_wallet_service.email;

public interface EmailService {

    void sendVerifyEmail(String to, String link);

    void sendResetPasswordEmail(String to, String newPassword);
}
