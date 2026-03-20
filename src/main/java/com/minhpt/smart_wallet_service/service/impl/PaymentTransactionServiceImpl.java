package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.request.CreatePaymentTransactionRequest;
import com.minhpt.smart_wallet_service.dto.response.PaymentTransactionResponse;
import com.minhpt.smart_wallet_service.mapper.PaymentTransactionMapper;
import com.minhpt.smart_wallet_service.model.PaymentTransaction;
import com.minhpt.smart_wallet_service.model.SubscriptionPlan;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.payment.vnpay.service.VnpayService;
import com.minhpt.smart_wallet_service.repository.PaymentTransactionRepository;
import com.minhpt.smart_wallet_service.repository.SubscriptionPlanRepository;
import com.minhpt.smart_wallet_service.service.PaymentTransactionService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import com.minhpt.smart_wallet_service.util.DataUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentTransactionMapper paymentTransactionMapper;
    private final AuthenticationUtil authenticationUtil;
    private final VnpayService vnpayService;

    @Override
    public PaymentTransactionResponse create(CreatePaymentTransactionRequest req, String ipAddress) {

        User user = authenticationUtil.getCurrentUser();
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository.findByIdAndStatus(req.getSubscriptionPlanId(), Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found by id = " + req.getSubscriptionPlanId()));

        String provider = DataUtil.normalize(req.getProvider());
        validationProiver(provider);

        PaymentTransaction paymentTransaction = PaymentTransaction.builder()
                .user(user)
                .subscriptionPlan(subscriptionPlan)
                .provider(provider)
                .orderCode(generatedOrderCode(provider))
                .amount(subscriptionPlan.getPrice())
                .state("PENDING")
                .providerRef(null)
                .payUrl(null)
                .rawRequest(null)
                .rawResponse(null)
                .paidAt(null)
                .deeplink(null)
                .qrCodeUrl(null)
                .build();

        paymentTransaction = paymentTransactionRepository.save(paymentTransaction);

        String payUrl;
        if (Constant.VNPAY.equals(provider)) {
            payUrl = vnpayService.createPaymentUrl(paymentTransaction, ipAddress);
        } else {
            throw new RuntimeException("Lỗi khi sinh payURL thông qua VNPAY");
        }

        paymentTransaction.setPayUrl(payUrl);
        log.info("payUrl length={}", len(paymentTransaction.getPayUrl()));
        log.info("providerRef length={}", len(paymentTransaction.getProviderRef()));
        log.info("deeplink length={}", len(paymentTransaction.getDeeplink()));
        log.info("qrCodeUrl length={}", len(paymentTransaction.getQrCodeUrl()));
        log.info("rawRequest length={}", len(paymentTransaction.getRawRequest()));
        log.info("rawResponse length={}", len(paymentTransaction.getRawResponse()));
        log.info("orderCode length={}", len(paymentTransaction.getOrderCode()));
        log.info("provider length={}", len(paymentTransaction.getProvider()));
        log.info("state length={}", len(paymentTransaction.getState()));
        return paymentTransactionMapper.toResponse(paymentTransactionRepository.save(paymentTransaction));
    }

    @Override
    public String handleVnpayReturn(Map<String, String> params) {
        boolean valid = vnpayService.verifyReturn(new HashMap<>(params));

        if (!valid) {
            throw new RuntimeException("Checksum invalid");
        }

        String orderCode = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");

        PaymentTransaction tx = paymentTransactionRepository
                .findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if ("SUCCESS".equals(tx.getState())) {
            return tx.getState();
        }

        if ("00".equals(responseCode)) {

            tx.setState("SUCCESS");
            tx.setProviderRef(transactionNo);
            tx.setPaidAt(LocalDateTime.now());

        } else {

            tx.setState("FAILED");
        }

        tx.setRawResponse(params.toString());

        paymentTransactionRepository.save(tx);

        return tx.getState();
    }


    private int len(String s) {
        return s == null ? 0 : s.length();
    }

    private String generatedOrderCode(String provider) {
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return provider + "_" + System.currentTimeMillis() + "_" + random;
    }

    private void validationProiver(String provider) {
        if(!provider.equals(Constant.VNPAY) && !provider.equals(Constant.MOMO)){
            throw new RuntimeException("Provider không hợp lệ. Chỉ hỗ trợ VNPAY hoặc MOMO");
        }
    }
}
