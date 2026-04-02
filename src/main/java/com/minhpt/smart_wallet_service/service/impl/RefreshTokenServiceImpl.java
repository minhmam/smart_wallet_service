package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.model.RefreshToken;
import com.minhpt.smart_wallet_service.repository.RefreshTokenRepository;
import com.minhpt.smart_wallet_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken create(Long userId) {

        refreshTokenRepository.deleteByUserId(userId);

        RefreshToken rt = new RefreshToken();
        rt.setId(userId);
        rt.setToken(UUID.randomUUID().toString());
        rt.setCreatedAt(LocalDateTime.now());
        rt.setExpireDate(LocalDateTime.now().plusDays(7));

        return refreshTokenRepository.save(rt);
    }

    @Override
    public RefreshToken verify(String token) {

        RefreshToken rt = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (rt.getExpireDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(rt);
            throw new RuntimeException("Expired refresh token");
        }

        return rt;
    }
}
