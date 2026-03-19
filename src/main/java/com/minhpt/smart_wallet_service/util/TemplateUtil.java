package com.minhpt.smart_wallet_service.util;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TemplateUtil {

    public static String loadTemplate(String path) {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Không đọc được file template: " + path, e);
        }
    }
}
