package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.email.EmailService;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.mapper.UserMapper;
import com.minhpt.smart_wallet_service.model.Role;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.service.AdminUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private static final int GENERATED_PASSWORD_LENGTH = 16;
    private static final String UPPERCASE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGIT_CHARS = "23456789";
    private static final String SPECIAL_CHARS = "@#$%&*!?";
    private static final String ALL_PASSWORD_CHARS = UPPERCASE_CHARS + LOWERCASE_CHARS + DIGIT_CHARS + SPECIAL_CHARS;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public UserResponse resetPassword(Long userId) {
        User user = getActiveUser(userId);
        String newPassword = generateStrongPassword();

        user.setPassword(passwordEncoder.encode(newPassword));
        User savedUser = userRepository.save(user);

        emailService.sendResetPasswordEmail(savedUser.getEmail(), newPassword);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse lock(Long userId) {
        User user = getActiveUser(userId);
        user.setStatus(Constant.DELETED);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse unlock(Long userId) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found by ID = " + userId));
        user.setStatus(Constant.NOT_DELETE);
        return userMapper.toResponse(userRepository.save(user));
    }

    private User getActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, Constant.NOT_DELETE)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by ID = " + userId));
    }

    private String generateStrongPassword() {
        char[] password = new char[GENERATED_PASSWORD_LENGTH];
        password[0] = randomChar(UPPERCASE_CHARS);
        password[1] = randomChar(LOWERCASE_CHARS);
        password[2] = randomChar(DIGIT_CHARS);
        password[3] = randomChar(SPECIAL_CHARS);

        for (int i = 4; i < GENERATED_PASSWORD_LENGTH; i++) {
            password[i] = randomChar(ALL_PASSWORD_CHARS);
        }

        shuffle(password);
        return new String(password);
    }

    private char randomChar(String chars) {
        return chars.charAt(secureRandom.nextInt(chars.length()));
    }

    private void shuffle(char[] values) {
        for (int i = values.length - 1; i > 0; i--) {
            int index = secureRandom.nextInt(i + 1);
            char temp = values[index];
            values[index] = values[i];
            values[i] = temp;
        }
    }
}
