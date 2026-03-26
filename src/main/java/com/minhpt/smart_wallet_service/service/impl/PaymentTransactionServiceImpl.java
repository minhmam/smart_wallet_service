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
import com.minhpt.smart_wallet_service.util.PaymentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

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

        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository
                .findByIdAndStatus(req.getSubscriptionPlanId(), Constant.NOT_DELETE)
                .orElseThrow(() -> new RuntimeException(
                        "Subscription plan not found by id = " + req.getSubscriptionPlanId()
                ));

        String provider = DataUtil.normalize(req.getProvider()).toUpperCase();
        validateProvider(provider);

        PaymentTransaction paymentTransaction = PaymentTransaction.builder()
                .user(user)
                .subscriptionPlan(subscriptionPlan)
                .provider(provider)
                .orderCode(PaymentUtil.generateOrderCode(provider))
                .amount(subscriptionPlan.getPrice())
                .state(Constant.STATE_PENDING)
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
            throw new RuntimeException("Unsupported payment method: " + provider);
        }

        paymentTransaction.setPayUrl(payUrl);
        paymentTransaction = paymentTransactionRepository.save(paymentTransaction);

        return paymentTransactionMapper.toResponse(paymentTransaction);
    }

    @Override
    public String handleVnpayReturn(HttpServletRequest request) {
        Map<String, String> params = getRequestParams(request);
        return vnpayService.handleReturn(params);
    }

    @Override
    public String handleVnpayIpn(HttpServletRequest request) {
        Map<String, String> params = getRequestParams(request);

        String result = vnpayService.handleIpn(params);

        if (isProcessableIpnResponse(result)) {
            processTransactionAfterCallback(params);
        }

        return result;
    }

    private void validateProvider(String provider) {
        if (provider == null || provider.isBlank()) {
            throw new IllegalArgumentException("Provider must not be blank");
        }

        if (!Constant.VNPAY.equalsIgnoreCase(provider)) {
            throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }


    private void processTransactionAfterCallback(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return;
        }

        if (!vnpayService.verifySignature(params)) {
            return;
        }

        String orderCode = params.get("vnp_TxnRef");
        if (orderCode == null || orderCode.isBlank()) {
            return;
        }

        PaymentTransaction transaction = paymentTransactionRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy giao dịch với orderCode: " + orderCode
                ));

        if (!Constant.STATE_PENDING.equalsIgnoreCase(transaction.getState())) {
            return;
        }

        String amountStr = params.get("vnp_Amount");
        if (amountStr == null || amountStr.isBlank()) {
            return;
        }

        long returnedAmount;
        try {
            returnedAmount = Long.parseLong(amountStr);
        } catch (NumberFormatException e) {
            return;
        }

        long expectedAmount = transaction.getAmount() * 100L;
        if (returnedAmount != expectedAmount) {
            return;
        }

        transaction.setProviderRef(params.get("vnp_TransactionNo"));
        transaction.setRawResponse(buildRawResponse(params));

        if (isSuccessResponse(params)) {
            transaction.setState(Constant.STATE_SUCCESS);

            String payDate = params.get("vnp_PayDate");
            if (payDate != null && !payDate.isBlank()) {
                transaction.setPaidAt(parsePayDate(payDate));
            }
        } else {
            transaction.setState(Constant.STATE_FAILED);
        }

        paymentTransactionRepository.save(transaction);
    }

    private boolean isSuccessResponse(Map<String, String> params) {
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");

        return "00".equals(responseCode) && "00".equals(transactionStatus);
    }

    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String fieldName = paramNames.nextElement();
            String fieldValue = request.getParameter(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        return fields;
    }

    private LocalDateTime parsePayDate(String payDate) {
        return LocalDateTime.parse(
                payDate,
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );
    }

    private String buildRawResponse(Map<String, String> fields) {
        StringBuilder raw = new StringBuilder();

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            raw.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }

        if (raw.length() > 0) {
            raw.setLength(raw.length() - 1);
        }

        return raw.toString();
    }

    private boolean isProcessableIpnResponse(String response) {
        return response != null && response.contains("\"RspCode\":\"00\"");
    }
}
