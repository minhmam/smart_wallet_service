package com.minhpt.smart_wallet_service.util;

public class ExceptionUtil {
    private ExceptionUtil() {
        throw new IllegalStateException("ExceptionUtil class");
    }
    public static String getMessageError(String errorCode) {
        return  errorCode.split("-")[1];
    }

    public static int getCodeError(String errorCode) {
        return Integer.parseInt(errorCode.split("-")[0]);

    }
}
