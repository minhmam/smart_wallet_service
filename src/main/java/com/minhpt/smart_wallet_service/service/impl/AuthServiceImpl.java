package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.LoginRequest;
import com.minhpt.smart_wallet_service.dto.response.AuthResponse;
import com.minhpt.smart_wallet_service.exception.AuthException;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import com.minhpt.smart_wallet_service.security.JwtService;
import com.minhpt.smart_wallet_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest req) {

        User user = userRepository.findByUsername(req.getUsername())
                .orElse(null);

        if (user == null
                || user.getStatus() != Constant.NOT_DELETE
                || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getRoleNames());
    }
}
