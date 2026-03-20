package com.minhpt.smart_wallet_service.payment.vnpay.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;

public class VnpayUtil {

    public static String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(data.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
