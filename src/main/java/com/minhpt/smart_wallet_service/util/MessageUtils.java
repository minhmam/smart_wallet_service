package com.minhpt.smart_wallet_service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

@Slf4j
public class MessageUtils {
    private MessageUtils() {
        throw new IllegalStateException("MessageUtils class");
    }
    private static final String BASE_NAME = "messages";

    public static String getMessage(String code, Locale locale) {
        String message=null;
        return getMessage(code, locale, message);
    }

    public static String getMessage(String code, Locale locale, Object... args) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(BASE_NAME, locale);
        String message;
        try {
            message = resourceBundle.getString(code);
            message = new String(message.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            message = MessageFormat.format(message, args);
        } catch (Exception ex) {
            log.error(">>> Can not get message with code {}", code);
            message = code;
        }
        return message;
    }

    public static String getMessage(String code) {
        String message=null;
        return getMessage(code, LocaleContextHolder.getLocale(), message);
    }

    public static String getMessage(String code, Object... args) {
        return getMessage(code, LocaleContextHolder.getLocale(), args);
    }
}
