package com.minhpt.smart_wallet_service.config;

import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationUtil {
    private final UserRepository userRepository;

    public User getCurrentUser() {

        //Get Token

        //Trả ra thông tin user đăng nhập
        return userRepository.findById(16L)
                .orElse(new User());
    }
}
