package com.minhpt.smart_wallet_service.util;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class TemplateUtil {

    public static String loadTemplate(String path) {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Không đọc được file template: " + path, e);
        }
    }

    public static String loadLocalizedTemplate(String path, Locale locale) {
        String localizedPath = resolveLocalizedPath(path, locale);
        if (!localizedPath.equals(path) && new ClassPathResource(localizedPath).exists()) {
            return loadTemplate(localizedPath);
        }
        return loadTemplate(path);
    }

    private static String resolveLocalizedPath(String path, Locale locale) {
        if (locale == null || !"en".equalsIgnoreCase(locale.getLanguage())) {
            return path;
        }

        int extensionIndex = path.lastIndexOf('.');
        if (extensionIndex < 0) {
            return path + "_en";
        }

        return path.substring(0, extensionIndex) + "_en" + path.substring(extensionIndex);
    }
}
