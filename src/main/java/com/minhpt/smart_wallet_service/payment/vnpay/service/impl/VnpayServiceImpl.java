package com.minhpt.smart_wallet_service.payment.vnpay.service.impl;

import com.minhpt.smart_wallet_service.model.PaymentTransaction;
import com.minhpt.smart_wallet_service.payment.vnpay.config.VnpayConfig;
import com.minhpt.smart_wallet_service.payment.vnpay.service.VnpayService;
import com.minhpt.smart_wallet_service.payment.vnpay.util.VnpayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VnpayServiceImpl implements VnpayService {

    private final VnpayConfig vnpayConfig;

    @Override
    public String createPaymentUrl(PaymentTransaction tx, String ipAddress) {
        Map<String, String> params = new HashMap<>();

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(tx.getAmount() * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", tx.getOrderCode());
        params.put("vnp_OrderInfo", "Thanh toan goi");
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);
        params.put("vnp_CreateDate",
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                        .format(LocalDateTime.now())
        );

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) {
                continue;
            }

            String encodedFieldName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8);
            String encodedFieldValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);

            hashData.append(encodedFieldName)
                    .append("=")
                    .append(encodedFieldValue)
                    .append("&");

            query.append(encodedFieldName)
                    .append("=")
                    .append(encodedFieldValue)
                    .append("&");
        }

        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }
        if (query.length() > 0) {
            query.setLength(query.length() - 1);
        }

        String secureHash = VnpayUtil.hmacSHA512(
                vnpayConfig.getSecretKey(),
                hashData.toString()
        );

        return vnpayConfig.getPayUrl()
                + "?"
                + query
                + "&vnp_SecureHash="
                + secureHash;
    }

    @Override
    public boolean verifyReturn(Map<String, String> params) {
        String vnpSecureHash = params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) {
                continue;
            }

            String encodedFieldName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8);
            String encodedFieldValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);

            hashData.append(encodedFieldName)
                    .append("=")
                    .append(encodedFieldValue)
                    .append("&");
        }

        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }

        String calculatedHash = VnpayUtil.hmacSHA512(
                vnpayConfig.getSecretKey(),
                hashData.toString()
        );

        return calculatedHash.equals(vnpSecureHash);
    }

    @Override
    public boolean verifyIpn(Map<String, String> params) {
        return verifyReturn(params);
    }
}
