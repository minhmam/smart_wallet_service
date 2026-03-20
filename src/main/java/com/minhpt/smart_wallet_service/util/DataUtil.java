package com.minhpt.smart_wallet_service.util;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public class DataUtil {
    public static LocalDateTime parseToLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }

        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        }

        if (value instanceof Date) {
            return ((Date) value)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        if (value instanceof String) {
            return LocalDateTime.parse((String) value);
        }

        if (value instanceof LocalDate) {
            return ((LocalDate) value).atStartOfDay();
        }

        return null;
    }

    public static String normalize(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new RuntimeException(data + " must be required");
        }
        return data.trim().toUpperCase(Locale.ROOT);
    }
}
