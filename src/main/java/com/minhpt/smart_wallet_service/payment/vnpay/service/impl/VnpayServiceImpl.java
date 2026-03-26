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

    private static final String VNP_SECURE_HASH = "vnp_SecureHash";
    private static final String VNP_SECURE_HASH_TYPE = "vnp_SecureHashType";
    private static final String VNP_RESPONSE_CODE = "vnp_ResponseCode";
    private static final String VNP_TRANSACTION_STATUS = "vnp_TransactionStatus";

    private final VnpayConfig vnpayConfig;

    @Override
    public String createPaymentUrl(PaymentTransaction tx, String ipAddress) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        Map<String, String> params = new HashMap<>();

        params.put("vnp_Version", vnpayConfig.getVersion());
        params.put("vnp_Command", vnpayConfig.getCommand());
        params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        params.put("vnp_Amount", String.valueOf(tx.getAmount() * 100L));
        params.put("vnp_CurrCode", vnpayConfig.getCurrCode());
        params.put("vnp_TxnRef", tx.getOrderCode());
        params.put("vnp_OrderInfo", vnpayConfig.getOrderInfo());
        params.put("vnp_OrderType", vnpayConfig.getOrderType());
        params.put("vnp_Locale", vnpayConfig.getLocale());
        params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        params.put("vnp_IpAddr", ipAddress);
        params.put("vnp_CreateDate", formatter.format(now));
        params.put("vnp_ExpireDate", formatter.format(now.plusMinutes(15)));

        String query = buildQueryData(params);
        String secureHash = VnpayUtil.hmacSHA512(vnpayConfig.getSecretKey(), query);

        return vnpayConfig.getPayUrl()
                + "?"
                + query
                + "&"
                + VNP_SECURE_HASH
                + "="
                + secureHash;
    }

    @Override
    public String handleReturn(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return vnpayConfig.getReturnMessageInvalidRequest();
        }

        if (!verifySignature(params)) {
            return vnpayConfig.getReturnMessageInvalidChecksum();
        }

        if (isSuccessResponse(params)) {
            return vnpayConfig.getReturnMessageSuccess();
        }

        return vnpayConfig.getReturnMessageFailed();
    }

    @Override
    public String handleIpn(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return buildIpnResponse(
                    vnpayConfig.getIpnResponseCodeInvalidRequest(),
                    vnpayConfig.getIpnMessageInvalidRequest()
            );
        }

        if (!verifySignature(params)) {
            return buildIpnResponse(
                    vnpayConfig.getIpnResponseCodeInvalidChecksum(),
                    vnpayConfig.getIpnMessageInvalidChecksum()
            );
        }

        if (isSuccessResponse(params)) {
            return buildIpnResponse(
                    vnpayConfig.getIpnResponseCodeHandled(),
                    vnpayConfig.getIpnMessageConfirmSuccess()
            );
        }

        return buildIpnResponse(
                vnpayConfig.getIpnResponseCodeHandled(),
                vnpayConfig.getIpnMessageConfirmFailed()
        );
    }

    @Override
    public boolean verifySignature(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return false;
        }

        String vnpSecureHash = params.get(VNP_SECURE_HASH);
        if (vnpSecureHash == null || vnpSecureHash.isBlank()) {
            return false;
        }

        Map<String, String> clonedParams = new HashMap<>(params);
        clonedParams.remove(VNP_SECURE_HASH);
        clonedParams.remove(VNP_SECURE_HASH_TYPE);

        String hashData = buildQueryData(clonedParams);
        String signValue = VnpayUtil.hmacSHA512(vnpayConfig.getSecretKey(), hashData);

        return signValue.equals(vnpSecureHash);
    }

    private boolean isSuccessResponse(Map<String, String> params) {
        String responseCode = params.get(VNP_RESPONSE_CODE);
        String transactionStatus = params.get(VNP_TRANSACTION_STATUS);

        return vnpayConfig.getSuccessCode().equals(responseCode)
                && vnpayConfig.getSuccessCode().equals(transactionStatus);
    }

    private String buildQueryData(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder data = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue == null || fieldValue.isEmpty()) {
                continue;
            }

            String encodedFieldName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8);
            String encodedFieldValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8);

            data.append(encodedFieldName)
                    .append("=")
                    .append(encodedFieldValue)
                    .append("&");
        }

        if (data.length() > 0) {
            data.setLength(data.length() - 1);
        }

        return data.toString();
    }

    private String buildIpnResponse(String rspCode, String message) {
        return "{\"RspCode\":\"" + rspCode + "\",\"Message\":\"" + message + "\"}";
    }

}
