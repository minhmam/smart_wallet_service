package com.minhpt.smart_wallet_service.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.minhpt.smart_wallet_service.dto.response.OcrTransactionResponse;
import com.minhpt.smart_wallet_service.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcrTransactionAiExtractor {

    private static final String DEFAULT_OPENAI_BASE_URL = "https://api.openai.com/v1/responses";
    private static final String TRANSACTION_DATE_REGEX = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$";

    private final ObjectMapper objectMapper;

    @Value("${openAi.api-key}")
    private String openAiApiKey;

    @Value("${openAi.model}")
    private String openAiModel;

    @Value("${openAi.base-url:" + DEFAULT_OPENAI_BASE_URL + "}")
    private String openAiBaseUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    public List<OcrTransactionResponse> extractTransactions(
            String inputText,
            String accountName,
            List<Category> categories
    ) {
        validateOpenAiConfig();

        if (categories == null || categories.isEmpty()) {
            throw new RuntimeException("Không tìm thấy category để AI mapping giao dịch");
        }

        try {
            String requestBody = objectMapper.writeValueAsString(buildRequestBody(inputText, accountName, categories));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(openAiBaseUrl))
                    .timeout(Duration.ofSeconds(60))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(resolveOpenAiErrorMessage(response.body(), response.statusCode()));
            }

            String outputText = extractOutputText(response.body());
            List<OcrTransactionResponse> transactions = objectMapper.readValue(outputText, new TypeReference<>() {
            });
            validateTransactions(transactions, categories);
            return transactions;
        } catch (IOException e) {
            throw new RuntimeException("Không parse được phản hồi từ OpenAI", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("OpenAI request bị gián đoạn", e);
        }
    }

    private ObjectNode buildRequestBody(String inputText, String accountName, List<Category> categories) {
        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", openAiModel);
        requestBody.put("store", false);

        ArrayNode input = requestBody.putArray("input");

        ObjectNode developerMessage = input.addObject();
        developerMessage.put("role", "developer");
        developerMessage.put("type", "message");
        ArrayNode developerContent = developerMessage.putArray("content");
        developerContent.addObject()
                .put("type", "input_text")
                .put("text", buildPrompt(accountName, categories));

        ObjectNode userMessage = input.addObject();
        userMessage.put("role", "user");
        userMessage.put("type", "message");
        ArrayNode userContent = userMessage.putArray("content");
        userContent.addObject()
                .put("type", "input_text")
                .put("text", "Đoạn text OCR cần trích xuất:\n" + inputText);

        ObjectNode text = requestBody.putObject("text");
        ObjectNode format = text.putObject("format");
        format.put("type", "json_schema");
        format.put("name", "ocr_transactions");
        format.put("strict", true);
        format.set("schema", buildSchema());

        return requestBody;
    }

    private ObjectNode buildSchema() {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("type", "array");

        ObjectNode item = root.putObject("items");
        item.put("type", "object");

        ObjectNode properties = item.putObject("properties");
        properties.putObject("categoryId").put("type", "integer");
        properties.putObject("description").put("type", "string");
        properties.putObject("amount").put("type", "integer");

        ObjectNode transactionDate = properties.putObject("transactionDate");
        ArrayNode transactionDateTypes = transactionDate.putArray("type");
        transactionDateTypes.add("string");
        transactionDateTypes.add("null");
        transactionDate.put("pattern", TRANSACTION_DATE_REGEX);

        ObjectNode type = properties.putObject("type");
        type.put("type", "string");
        ArrayNode enums = type.putArray("enum");
        enums.add("INCOME");
        enums.add("EXPENSE");

        ArrayNode required = item.putArray("required");
        required.add("categoryId");
        required.add("description");
        required.add("amount");
        required.add("transactionDate");
        required.add("type");

        item.put("additionalProperties", false);
        return root;
    }

    private String buildPrompt(String accountName, List<Category> categories) {
        String resolvedAccountName = (accountName == null || accountName.isBlank())
                ? "Không rõ"
                : accountName.trim();

        StringBuilder categoryPrompt = new StringBuilder();
        for (Category category : categories) {
            categoryPrompt
                    .append(category.getId())
                    .append(" = ")
                    .append(category.getName());

            if (category.getType() != null && !category.getType().isBlank()) {
                categoryPrompt.append(" (").append(category.getType()).append(")");
            }

            categoryPrompt.append("\n");
        }

        return """
                Bạn là AI chuyên trích xuất dữ liệu tài chính từ văn bản.

                Hãy chuyển đoạn text dưới đây thành JSON theo đúng format:

                [
                  {
                    "categoryId": number,
                    "description": string,
                    "amount": number,
                    "transactionDate": "dd/MM/yyyy",
                    "type": "INCOME" | "EXPENSE"
                  }
                ]

                Yêu cầu:
                - CHỈ trả về JSON, không giải thích, không thêm text khác
                - amount là số nguyên (loại bỏ dấu phẩy, dấu chấm phân cách)
                - transactionDate phải đúng định dạng dd/MM/yyyy (nếu không có thì để null)
                - description là mô tả ngắn gọn nội dung giao dịch
                - categoryId phải chọn từ danh sách category bên dưới bằng ID thật, không tự tạo ID mới

                categoryId map:
                %s
                type:
                - INCOME nếu là thu nhập
                - EXPENSE nếu là chi tiêu

                Quy tắc suy luận:

                1. Hóa đơn mua hàng → EXPENSE

                2. Chuyển khoản ngân hàng:
                - Nếu có các từ: "nhận", "nhận tiền", "credit", "tiền vào", "received", "nap", "deposit" → INCOME
                - Nếu có các từ: "chuyển", "chuyển khoản", "debit", "tiền ra", "paid", "transfer", "thanh toán" → EXPENSE

                3. Ưu tiên nhận diện theo ngữ cảnh:
                - Nếu có tên người gửi đến tôi → INCOME
                - Nếu có tên người nhận từ tôi → EXPENSE
                - Tên tôi là: %s

                4. Nếu không xác định rõ → mặc định EXPENSE
                """.formatted(categoryPrompt.toString().trim(), resolvedAccountName);
    }

    private String extractOutputText(String responseBody) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode outputNodes = root.path("output");

        for (JsonNode outputNode : outputNodes) {
            JsonNode contentNodes = outputNode.path("content");
            for (JsonNode contentNode : contentNodes) {
                if ("output_text".equals(contentNode.path("type").asText())) {
                    String text = contentNode.path("text").asText();
                    if (text != null && !text.isBlank()) {
                        return text;
                    }
                }
            }
        }

        throw new RuntimeException("OpenAI không trả về nội dung transaction hợp lệ");
    }

    private String resolveOpenAiErrorMessage(String responseBody, int statusCode) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String message = root.path("error").path("message").asText();
            if (message != null && !message.isBlank()) {
                return "OpenAI request failed (%s): %s".formatted(statusCode, message);
            }
        } catch (JsonProcessingException ignored) {
        }

        return "OpenAI request failed with status " + statusCode;
    }

    private void validateOpenAiConfig() {
        if (openAiApiKey == null || openAiApiKey.isBlank() || "sk-".equals(openAiApiKey.trim())) {
            throw new RuntimeException("OpenAI API key chưa được cấu hình");
        }
    }

    private void validateTransactions(List<OcrTransactionResponse> transactions, List<Category> categories) {
        if (transactions == null) {
            throw new RuntimeException("OpenAI không trả về danh sách transaction");
        }

        Set<Long> validCategoryIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        for (OcrTransactionResponse transaction : transactions) {
            if (transaction.getCategoryId() == null || !validCategoryIds.contains(transaction.getCategoryId())) {
                throw new RuntimeException("OpenAI trả về categoryId không tồn tại trong danh sách category");
            }

            if (transaction.getDescription() == null || transaction.getDescription().isBlank()) {
                throw new RuntimeException("OpenAI trả về description rỗng");
            }

            if (transaction.getAmount() == null || transaction.getAmount() < 0) {
                throw new RuntimeException("OpenAI trả về amount không hợp lệ");
            }

            if (transaction.getType() == null ||
                    (!"INCOME".equals(transaction.getType()) && !"EXPENSE".equals(transaction.getType()))) {
                throw new RuntimeException("OpenAI trả về type không hợp lệ");
            }

            if (transaction.getTransactionDate() != null &&
                    !transaction.getTransactionDate().matches(TRANSACTION_DATE_REGEX)) {
                throw new RuntimeException("OpenAI trả về transactionDate không đúng định dạng dd/MM/yyyy");
            }
        }
    }
}
