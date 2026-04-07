package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.exception.ResourceNotFoundException;
import com.minhpt.smart_wallet_service.service.VerificationTokenService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.UserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.mapper.UserMapper;
import com.minhpt.smart_wallet_service.model.AccountBalance;
import com.minhpt.smart_wallet_service.model.Role;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.AccountBalanceRepository;
import com.minhpt.smart_wallet_service.repository.RoleRepository;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;
    private static final int MAX_FULL_NAME_LENGTH = 100;
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9._-]+$";
    private static final String PASSWORD_UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String PASSWORD_LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String PASSWORD_DIGIT_PATTERN = ".*\\d.*";
    private static final String PASSWORD_SPECIAL_PATTERN = ".*[^A-Za-z0-9].*";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationUtil authenticationUtil;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final AccountBalanceRepository accountBalanceRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest req) {
        validateRegister(req);
        User user = userMapper.toEntity(req);

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setVerified(Constant.NOT_VERIFIED);
        user.setCreatedBy(Constant.USER_DEFAULT);
        user.setUpdatedBy(Constant.USER_DEFAULT);

        User savedUser = userRepository.save(user);
        Role defaultRole = getOrCreateRole(Constant.ROLE_USER);
        savedUser.addRole(defaultRole);

        if (shouldGrantBootstrapAdmin()) {
            Role adminRole = getOrCreateRole(Constant.ROLE_ADMIN);
            savedUser.addRole(adminRole);
        }

        savedUser = userRepository.save(savedUser);

        createInitialAccountBalance(savedUser);

        verificationTokenService.sendVerifyEmail(savedUser);

        return userMapper.toResponse(savedUser);
    }

    private void validateRegister(UserCreateRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Register request must not be null");
        }

        String username = normalizeUsername(req.getUsername());
        String email = normalizeEmail(req.getEmail());
        String password = req.getPassword();

        if (username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Username length must be between " + MIN_USERNAME_LENGTH + " and " + MAX_USERNAME_LENGTH + " characters"
            );
        }

        if (!username.matches(USERNAME_PATTERN)) {
            throw new IllegalArgumentException("Username may only contain letters, numbers, dot, underscore, or hyphen");
        }

        if (email.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("Email length must not exceed " + MAX_EMAIL_LENGTH + " characters");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }

        if (password.contains(" ")) {
            throw new IllegalArgumentException("Password must not contain spaces");
        }

        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password length must be between " + MIN_PASSWORD_LENGTH + " and " + MAX_PASSWORD_LENGTH + " characters"
            );
        }

        if (!password.matches(PASSWORD_UPPERCASE_PATTERN)
                || !password.matches(PASSWORD_LOWERCASE_PATTERN)
                || !password.matches(PASSWORD_DIGIT_PATTERN)
                || !password.matches(PASSWORD_SPECIAL_PATTERN)) {
            throw new IllegalArgumentException(
                    "Password must contain uppercase, lowercase, number, and special character"
            );
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        req.setUsername(username);
        req.setEmail(email);
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be blank");
        }
        return username.trim();
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

        String normalizedFullName = fullName.trim();
        if (normalizedFullName.length() > MAX_FULL_NAME_LENGTH) {
            throw new IllegalArgumentException("Full name must not exceed " + MAX_FULL_NAME_LENGTH + " characters");
        }

        return normalizedFullName;
    }

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number must not be blank");
        }

        return phoneNumber.trim();
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserUpdateRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Update request must not be null");
        }

        User userLogin = authenticationUtil.getCurrentUser();

        User updateUser = userRepository.findById(userLogin.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found by ID = " + userLogin.getId()));

        boolean hasChanges = false;

        if (req.getFullName() != null) {
            updateUser.setFullName(normalizeFullName(req.getFullName()));
            hasChanges = true;
        }

        if (req.getPhoneNumber() != null) {
            updateUser.setPhoneNumber(normalizePhoneNumber(req.getPhoneNumber()));
            hasChanges = true;
        }

        if (!hasChanges) {
            throw new IllegalArgumentException("At least one field must be provided");
        }

        updateUser.setUpdatedBy(userLogin.getUsername());

        return userMapper.toResponse(userRepository.save(updateUser));
    }

    @Override
    public UserResponse getById() {
        User userLogin = authenticationUtil.getCurrentUser();

        User detailUser = userRepository.findById(userLogin.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found by id = " + userLogin.getId()));

        return userMapper.toResponse(detailUser);
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(roleName)
                                .build()
                ));
    }

    private void createInitialAccountBalance(User user) {
        AccountBalance accountBalance = AccountBalance.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();
        accountBalance.setCreatedBy(user.getUsername());
        accountBalance.setUpdatedBy(user.getUsername());
        accountBalanceRepository.save(accountBalance);
    }

    private boolean shouldGrantBootstrapAdmin() {
        return userRepository.findAll()
                .stream()
                .noneMatch(existingUser -> existingUser.getRoleNames().contains(Constant.ROLE_ADMIN));
    }
}
