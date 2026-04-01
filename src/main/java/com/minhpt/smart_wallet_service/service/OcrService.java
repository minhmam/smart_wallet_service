package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.response.OcrExtractResponse;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    String extractText(MultipartFile multipartFile);

    OcrExtractResponse extractTransactions(MultipartFile multipartFile);
}
