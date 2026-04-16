package com.minhpt.smart_wallet_service.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.minhpt.smart_wallet_service.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private Object total;
    private LocalDateTime timestamp;
    private String path;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(Constant.SUCCESS)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<List<T>> successPage(List<T> data, long total) {
        return ApiResponse.<List<T>>builder()
                .status(200)
                .message(Constant.SUCCESS)
                .data(data)
                .total(total)
                .build();
    }

    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> success() {
        return (ApiResponse<T>) ApiResponse.builder()
                .status(200)
                .message(Constant.SUCCESS)
                .build();
    }
}
