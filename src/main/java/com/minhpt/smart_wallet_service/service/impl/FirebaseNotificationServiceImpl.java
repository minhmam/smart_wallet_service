package com.minhpt.smart_wallet_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minhpt.smart_wallet_service.dto.response.FirebasePushResult;
import com.minhpt.smart_wallet_service.model.NotificationSchedule;
import com.minhpt.smart_wallet_service.model.UserNotificationToken;
import com.minhpt.smart_wallet_service.service.FirebaseNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FirebaseNotificationServiceImpl implements FirebaseNotificationService {

    private static final String FIREBASE_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String DEFAULT_TOKEN_URI = "https://oauth2.googleapis.com/token";

    private final ObjectMapper objectMapper;

    @Value("${firebase.enabled:false}")
    private boolean firebaseEnabled;

    @Value("${firebase.project-id:}")
    private String projectId;

    @Value("${firebase.service-account-path:}")
    private String serviceAccountPath;

    private String cachedAccessToken;
    private Instant cachedAccessTokenExpiresAt = Instant.EPOCH;

    @Override
    public FirebasePushResult send(NotificationSchedule schedule, List<UserNotificationToken> tokens) {
        long targetUserCount = tokens.stream()
                .map(UserNotificationToken::getUser)
                .filter(Objects::nonNull)
                .map(user -> user.getId())
                .filter(Objects::nonNull)
                .distinct()
                .count();

        if (tokens.isEmpty()) {
            return FirebasePushResult.builder()
                    .targetUserCount(targetUserCount)
                    .successCount(0L)
                    .failureCount(0L)
                    .build();
        }

        validateFirebaseConfig();

        String accessToken = getAccessToken();
        long successCount = 0L;
        long failureCount = 0L;

        for (UserNotificationToken token : tokens) {
            try {
                sendToToken(schedule, token.getFcmToken(), accessToken);
                successCount++;
            } catch (RuntimeException ex) {
                failureCount++;
            }
        }

        return FirebasePushResult.builder()
                .targetUserCount(targetUserCount)
                .successCount(successCount)
                .failureCount(failureCount)
                .build();
    }

    private void validateFirebaseConfig() {
        if (!firebaseEnabled) {
            throw new IllegalStateException("Firebase notification is disabled");
        }

        if (!StringUtils.hasText(projectId)) {
            throw new IllegalStateException("Firebase project id is not configured");
        }

        if (!StringUtils.hasText(serviceAccountPath)) {
            throw new IllegalStateException("Firebase service account path is not configured");
        }
    }

    private synchronized String getAccessToken() {
        if (StringUtils.hasText(cachedAccessToken)
                && cachedAccessTokenExpiresAt.isAfter(Instant.now().plusSeconds(60))) {
            return cachedAccessToken;
        }

        try {
            Map<String, Object> serviceAccount = loadServiceAccount();
            String tokenUri = Objects.toString(serviceAccount.getOrDefault("token_uri", DEFAULT_TOKEN_URI));
            String assertion = buildJwtAssertion(serviceAccount, tokenUri);
            String body = "grant_type=" + encode("urn:ietf:params:oauth:grant-type:jwt-bearer")
                    + "&assertion=" + encode(assertion);

            HttpURLConnection connection = openConnection(tokenUri, "POST", "application/x-www-form-urlencoded");
            writeBody(connection, body);
            Map<String, Object> response = readJsonResponse(connection);

            cachedAccessToken = Objects.toString(response.get("access_token"), null);
            Number expiresIn = response.get("expires_in") instanceof Number ? (Number) response.get("expires_in") : BigDecimal.valueOf(3600L);
            cachedAccessTokenExpiresAt = Instant.now().plusSeconds(expiresIn.longValue());

            if (!StringUtils.hasText(cachedAccessToken)) {
                throw new IllegalStateException("Firebase access token response is invalid");
            }

            return cachedAccessToken;
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot get Firebase access token", ex);
        }
    }

    private Map<String, Object> loadServiceAccount() throws IOException {
        return objectMapper.readValue(Files.readString(Path.of(serviceAccountPath)), Map.class);
    }

    private String buildJwtAssertion(Map<String, Object> serviceAccount, String tokenUri) throws IOException {
        String clientEmail = Objects.toString(serviceAccount.get("client_email"), "");
        String privateKey = Objects.toString(serviceAccount.get("private_key"), "");

        if (!StringUtils.hasText(clientEmail) || !StringUtils.hasText(privateKey)) {
            throw new IllegalStateException("Firebase service account file is missing client_email or private_key");
        }

        Instant now = Instant.now();
        Map<String, Object> header = Map.of(
                "alg", "RS256",
                "typ", "JWT"
        );
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("iss", clientEmail);
        payload.put("scope", FIREBASE_SCOPE);
        payload.put("aud", tokenUri);
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", now.plusSeconds(3600).getEpochSecond());

        String unsignedJwt = base64Url(objectMapper.writeValueAsBytes(header))
                + "."
                + base64Url(objectMapper.writeValueAsBytes(payload));

        return unsignedJwt + "." + sign(unsignedJwt, privateKey);
    }

    private String sign(String unsignedJwt, String privateKeyPem) {
        try {
            String normalizedKey = privateKeyPem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] privateKeyBytes = Base64.getDecoder().decode(normalizedKey);
            PrivateKey privateKey = KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(unsignedJwt.getBytes(StandardCharsets.UTF_8));
            return base64Url(signature.sign());
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot sign Firebase JWT assertion", ex);
        }
    }

    private void sendToToken(NotificationSchedule schedule, String fcmToken, String accessToken) {
        if (!StringUtils.hasText(fcmToken)) {
            throw new IllegalArgumentException("FCM token must not be blank");
        }

        try {
            String url = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
            HttpURLConnection connection = openConnection(url, "POST", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);

            Map<String, Object> notification = Map.of(
                    "title", schedule.getTitle(),
                    "body", schedule.getContent()
            );
            Map<String, String> data = Map.of(
                    "scheduleId", String.valueOf(schedule.getId()),
                    "targetGroup", schedule.getTargetGroup()
            );
            Map<String, Object> message = Map.of(
                    "token", fcmToken,
                    "notification", notification,
                    "data", data
            );
            Map<String, Object> request = Map.of("message", message);

            writeBody(connection, objectMapper.writeValueAsString(request));
            readJsonResponse(connection);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot send Firebase notification", ex);
        }
    }

    private HttpURLConnection openConnection(String url, String method, String contentType) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(20000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", contentType);
        return connection;
    }

    private void writeBody(HttpURLConnection connection, String body) throws IOException {
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body.getBytes(StandardCharsets.UTF_8));
        }
    }

    private Map<String, Object> readJsonResponse(HttpURLConnection connection) throws IOException {
        int statusCode = connection.getResponseCode();
        InputStream responseStream = statusCode >= 400 ? connection.getErrorStream() : connection.getInputStream();
        String responseBody = responseStream == null
                ? ""
                : new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

        if (statusCode >= 400) {
            throw new IllegalStateException("Firebase request failed: " + truncate(responseBody));
        }

        if (!StringUtils.hasText(responseBody)) {
            return Map.of();
        }

        return objectMapper.readValue(responseBody, Map.class);
    }

    private String base64Url(byte[] value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }

        return value.length() > 500 ? value.substring(0, 500) : value;
    }
}
