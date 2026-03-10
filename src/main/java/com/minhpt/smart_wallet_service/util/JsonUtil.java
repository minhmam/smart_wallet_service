//package com.minhpt.smart_wallet_service.util;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicReference;
//@Slf4j
//public class JsonUtil {
//    private static final AtomicReference<ObjectMapper> objectMapper = new AtomicReference<>();
//
//    @Autowired
//    private JsonUtil(ObjectMapper objectMapper) {
//        JsonUtil.objectMapper.set(objectMapper);
//    }
//
//
//    public static String toJsonString(Object object){
//        if(object == null) return null;
//        try {
//            return objectMapper.get().writeValueAsString(object);
//        } catch (JsonProcessingException e) {
//            log.error("Error write object as string", e);
//            return null;
//        }
//    }
//
//    public static <T> T toObject(Map<String, Object> map, Class<T> targetType){
//        return toObject(toJsonString(map), targetType);
//    }
//
//    public static <T> T toObject(String jsonString, Class<T> targetType){
//        if(jsonString == null || jsonString.isEmpty()) return null;
//        try {
//            return objectMapper.get().readValue(jsonString, targetType);
//        } catch (JsonProcessingException e) {
//            log.error("Error write object as string", e);
//            return null;
//        }
//    }
//
//    public static <T> T toObject(File jsonFile, Class<T> targetType){
//        if(jsonFile == null || !jsonFile.exists()) return null;
//        try {
//            return objectMapper.get().readValue(jsonFile, targetType);
//        } catch (IOException e) {
//            log.error(e.getMessage());
//            return null;
//        }
//    }
//}
