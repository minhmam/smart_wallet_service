package com.minhpt.smart_wallet_service.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private DateUtil() {
        throw new IllegalStateException("DateUtil class");
    }
    public static String getCurrentDateYYYYMMDD() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("YYYYMMdd");
        return dtf.format(LocalDate.now());
    }

    public static Date convertToDateUsingDate(LocalDate date) {
        return java.sql.Date.valueOf(date);
    }

    public static Date convertToDateUsingInstant(LocalDate date) {
        return Date.from(date.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

}
