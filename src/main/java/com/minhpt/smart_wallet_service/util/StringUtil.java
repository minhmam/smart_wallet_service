package com.minhpt.smart_wallet_service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.util.Objects;
import java.util.regex.Pattern;

@Slf4j
public class StringUtil {
  private static ObjectMapper mapper;
  private StringUtil() {
    throw new IllegalStateException("StringUtil class");
  }
  static {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public static String removeAccents(String value) {
    if (value == null) {
      return null;
    }
    value = value.replace("\u0110", "D").replace("\u0111", "d");
    String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
    Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    return pattern.matcher(temp).replaceAll("");
  }

  public static String keepAlphaNumberic(String value) {
    if (value == null) {
      return null;
    }
    return value.replaceAll("[^A-Za-z0-9]", "");
  }

  public static String keepAlphaNumbericNewLine(String value) {
    if (value == null) {
      return null;
    }
    return value.replaceAll("[^A-Za-z0-9\\n]", "");
  }

  public static String toFullTextSearch(String value) {
    if (value == null) {
      return null;
    }
    return keepAlphaNumbericNewLine(removeAccents(value.toLowerCase()));
  }

  public static String serializeObject(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
        log.error("Error write object as string", e);
      return null;
    }
  }

  public static <T> T deserializeObject(String str, Class<T> clazz) {
    try {
      return mapper.readValue(str, clazz);
    } catch (JsonProcessingException e) {
        log.error("Error read json string as class " + clazz.getName() , e);
      return null;
    }
  }

  public static boolean isEmpty(String text) {
    return Objects.isNull(text) || text.trim().length() == 0;
  }
}
