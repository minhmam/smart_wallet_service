package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.AdminUserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.email.EmailService;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.mapper.UserMapper;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.service.AdminUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Locale;

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
    public UserResponse getDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by ID = " + userId));
        return userMapper.toResponse(user);
    }

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
    public UserResponse update(Long userId, AdminUserUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Update request must not be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by ID = " + userId));

        boolean hasChanges = false;

        if (request.getEmail() != null) {
            String email = normalizeEmail(request.getEmail());
            if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, userId)) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(email);
            hasChanges = true;
        }

        if (request.getFullName() != null) {
            user.setFullName(normalizeFullName(request.getFullName()));
            hasChanges = true;
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(normalizePhoneNumber(request.getPhoneNumber()));
            hasChanges = true;
        }

        if (!hasChanges) {
            throw new IllegalArgumentException("At least one field must be provided");
        }

        return userMapper.toResponse(userRepository.save(user));
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

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name must not be blank");
        }

        return fullName.trim();
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be blank");
        }

        return phoneNumber.trim();
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
