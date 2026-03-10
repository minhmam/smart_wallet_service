//package com.minhpt.smart_wallet_service.util;
//
//import com.fasterxml.jackson.databind.DeserializationFeature;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//
//import java.util.Map;
//
//public class MappingObjectUtil {
//    private MappingObjectUtil() {
//        throw new IllegalStateException("MappingObjectUtil class");
//    }
//    private static final ObjectMapper mapper = new ObjectMapper();
//
//    static GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
//
//    static {
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        mapper.registerModule(javaTimeModule);
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.findAndRegisterModules();
//    }
//
//    @SuppressWarnings("unchecked")
//    public static Map<String, Object> convertObjectToMap(Object object) {
//        return mapper.convertValue(object, Map.class);
//    }
//
//
//    public static byte[] convertObjectToBytes(Object object) {
//        return serializer.serialize(object);
//    }
//}
