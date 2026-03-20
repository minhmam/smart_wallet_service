package com.minhpt.smart_wallet_service.util;

import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUtil {
    public User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new RuntimeException("User chưa đăng nhập");
        }

        return (User) authentication.getPrincipal();
    }
}
