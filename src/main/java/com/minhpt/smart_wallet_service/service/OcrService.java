package com.minhpt.smart_wallet_service.service;

import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    public String extractText(MultipartFile multipartFile);
}
