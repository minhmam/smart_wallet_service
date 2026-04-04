package com.minhpt.smart_wallet_service.config;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.model.Role;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.RoleRepository;
import com.minhpt.smart_wallet_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleBootstrap implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Role userRole = getOrCreateRole(Constant.ROLE_USER);
        Role adminRole = getOrCreateRole(Constant.ROLE_ADMIN);

        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (!user.getUserRoles().isEmpty()) {
                continue;
            }
            user.addRole(userRole);
        }

        boolean hasAdmin = users.stream()
                .anyMatch(user -> user.getRoleNames().contains(Constant.ROLE_ADMIN));

        if (!hasAdmin && !users.isEmpty()) {
            users.get(0).addRole(adminRole);
        }
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(roleName)
                                .build()
                ));
    }
}
