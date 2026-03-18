package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.service.VerificationTokenService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import com.minhpt.smart_wallet_service.dto.request.UserCreateRequest;
import com.minhpt.smart_wallet_service.dto.request.UserUpdateRequest;
import com.minhpt.smart_wallet_service.dto.response.UserResponse;
import com.minhpt.smart_wallet_service.mapper.UserMapper;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationUtil authenticationUtil;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;

    @Override
    public UserResponse createUser(UserCreateRequest req) {

        User user = userMapper.toEntity(req);

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole("USER");
        user.setIsEmailVerified(Constant.NOT_VERIFIED);

        User savedUser = userRepository.save(user);

        verificationTokenService.sendVerifyEmail(savedUser);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest req) {
        User userLogin = authenticationUtil.getCurrentUser();

        User updateUser = userRepository.findById(userLogin.getId())
                .orElseThrow(() -> new RuntimeException("User not found by ID = " + userLogin.getId()));

        if(req.getFullName() != null){
            updateUser.setFullName(req.getFullName());
        }

        if(req.getPhoneNumber() != null){
            updateUser.setPhoneNumber(req.getPhoneNumber());
        }

        if(req.getEmail() != null){
            updateUser.setEmail(req.getEmail());
        }

        updateUser.setUpdatedAt(LocalDateTime.now());
        updateUser.setUpdatedBy("system");

        userRepository.save(updateUser);

        return userMapper.toResponse(updateUser);
    }

    @Override
    public UserResponse getById() {
        User userLogin = authenticationUtil.getCurrentUser();

        User detailUser = userRepository.findById(userLogin.getId())
                .orElseThrow(() -> new RuntimeException("User not found by id + " + userLogin.getId()));

        return userMapper.toResponse(detailUser);
    }
}
