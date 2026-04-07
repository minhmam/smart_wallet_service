package com.minhpt.smart_wallet_service.i18n;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice(annotations = Controller.class)
public class ApiResponseLocalizationAdvice implements ResponseBodyAdvice<Object> {

    private final MessageResolver messageResolver;

    public ApiResponseLocalizationAdvice(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (!(body instanceof ApiResponse<?> apiResponse)) {
            return body;
        }

        ApiResponse<Object> localizedResponse = (ApiResponse<Object>) apiResponse;
        localizedResponse.setMessage(messageResolver.resolve(localizedResponse.getMessage()));

        if (localizedResponse.getStatus() == HttpStatus.BAD_REQUEST.value()
                && localizedResponse.getData() instanceof Map<?, ?> validationErrors) {
            localizedResponse.setData(localizeStringValues(validationErrors));
        }

        return localizedResponse;
    }

    private Map<Object, Object> localizeStringValues(Map<?, ?> source) {
        Map<Object, Object> localizedValues = new LinkedHashMap<>();

        for (Map.Entry<?, ?> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String message) {
                localizedValues.put(entry.getKey(), messageResolver.resolve(message));
                continue;
            }
            localizedValues.put(entry.getKey(), value);
        }

        return localizedValues;
    }
}
