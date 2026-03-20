package com.minhpt.smart_wallet_service.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    public static String getClientIp(HttpServletRequest request) {

        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }

        return xfHeader.split(",")[0];
    }
}
