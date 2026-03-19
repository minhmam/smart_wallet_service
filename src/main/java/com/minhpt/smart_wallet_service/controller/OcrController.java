package com.minhpt.smart_wallet_service.controller;

import com.minhpt.smart_wallet_service.common.ApiResponse;
import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrService ocrService;

    @PostMapping("/extract")
    public ResponseEntity<?> extract(@RequestParam("file")MultipartFile file){

        String text = ocrService.extractText(file);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message(Constant.SUCCESS)
                        .data(Map.of("text", text))
                        .build()
        );
    }
}
