package com.minhpt.smart_wallet_service.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PaymentUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Ho_Chi_Minh");

    private PaymentUtil() {
    }

    public static String generateOrderCode(String provider) {

        String normalizedProvider = DataUtil.normalize(provider).toUpperCase();
        String timePart = LocalDateTime.now(ZONE_ID).format(FORMATTER);
        String randomPart = UUID.randomUUID().toString()
                .replaceAll("[^A-Za-z0-9]", "")
                .substring(0, 8)
                .toUpperCase();

        return normalizedProvider + "_" + timePart + "_" + randomPart;

    }
}
