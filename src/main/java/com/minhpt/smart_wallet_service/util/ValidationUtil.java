package com.minhpt.smart_wallet_service.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

public class ValidationUtil {
    public static final String IS_NOT_EMPTY = "_IS_NOT_EMPTY";
    public static final String IS_OUT_OF_RANGE = "_IS_OUT_OF_RANGE";
    private ValidationUtil() {
        throw new IllegalStateException("ValidationUtil class");
    }

    public static void validateObjectValue(String name, Object value, Boolean required, Dictionary errors){
            if(Boolean.TRUE.equals(required)&&value == null) errors.put(name.toUpperCase() + "_IS_NOT_NULL");
    }

    public static void validateStringValue(String name, String value, Integer maxLength, Boolean required, List<String> errors){
            if(Boolean.TRUE.equals(required)&&StringUtils.isEmpty(value)) errors.add(name.toUpperCase() + IS_NOT_EMPTY);
        if(!StringUtils.isEmpty(value) && value.length() > maxLength) errors.add(name.toUpperCase() + IS_OUT_OF_RANGE);
    }
    public static void validateStringValue(String name, String value, Integer maxLength, Boolean required, Dictionary errors){
            if(Boolean.TRUE.equals(required)&&StringUtils.isEmpty(value)) errors.put(name.toUpperCase() + IS_NOT_EMPTY);
        if(!StringUtils.isEmpty(value) && value.length() > maxLength) errors.put(name.toUpperCase() + IS_OUT_OF_RANGE);
    }

    public static void validateIntegerValue(String name, Integer value, Integer maxLength, Boolean required, List<String> errors){
            if((value == null || value <= 0)&&Boolean.TRUE.equals(required)) errors.add(name.toUpperCase() + IS_NOT_EMPTY);
        if(value != null && value >= Math.pow(10, maxLength)) errors.add(name.toUpperCase() + IS_OUT_OF_RANGE);
    }

    public static void validateIntegerValue(String name, Integer value, Integer maxLength, Boolean required, Dictionary errors){
            if((value == null || value <= 0)&&Boolean.TRUE.equals(required)) errors.put(name.toUpperCase() + IS_NOT_EMPTY);
        if(value != null && value >= Math.pow(10, maxLength)) errors.put(name.toUpperCase() + IS_OUT_OF_RANGE);
    }

    public static void validateLongValue(String name, Long value, Integer maxLength, Boolean required, Dictionary errors){
            if((value == null || value <= 0)&&Boolean.TRUE.equals(required)) errors.put(name.toUpperCase() + IS_NOT_EMPTY);
        if(value != null && value >= Math.pow(10, maxLength)) errors.put(name.toUpperCase() + IS_OUT_OF_RANGE);
    }

    public static void validateDoubleValue(String name, Double value, Integer maxLength, Boolean required, Dictionary errors){
            if(Boolean.TRUE.equals(required)&&(value == null || value <= 0)) errors.put(name.toUpperCase() + IS_NOT_EMPTY);
        if(value != null && value >= Math.pow(10, maxLength)) errors.put(name.toUpperCase() + IS_OUT_OF_RANGE);
    }

    public static void validateDateValue(String name, Date value, Boolean required, List<String> errors){
            if(Boolean.TRUE.equals(required)&&value == null) errors.add(name.toUpperCase() + IS_NOT_EMPTY);
    }

    public static void validateDateValue(String name, Date value, Boolean required, Dictionary errors){
            if(Boolean.TRUE.equals(required)&&value == null) errors.put(name.toUpperCase() + IS_NOT_EMPTY);
    }

    public static void validateValueEquals(String name, boolean isTrue, Dictionary errors){
        if(Boolean.TRUE.equals(!isTrue)){
            errors.put(name.toUpperCase() + "_WRONG_VALUE");
        }
    }
}
