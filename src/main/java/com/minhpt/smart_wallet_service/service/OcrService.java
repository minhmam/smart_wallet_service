package com.minhpt.smart_wallet_service.service;

import com.minhpt.smart_wallet_service.dto.response.OcrTransactionDraftResponse;
import com.minhpt.smart_wallet_service.dto.response.OcrTransactionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
    String extractText(MultipartFile multipartFile);

    OcrTransactionDraftResponse extractTransaction(MultipartFile multipartFile);
}
