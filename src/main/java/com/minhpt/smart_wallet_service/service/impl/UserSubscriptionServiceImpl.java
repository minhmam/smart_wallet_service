package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.model.UserSubscription;
import com.minhpt.smart_wallet_service.repository.UserSubscriptionRepository;
import com.minhpt.smart_wallet_service.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Override
    public void create(UserSubscription userSubscription) {

        userSubscriptionRepository.save(userSubscription);
    }
}
