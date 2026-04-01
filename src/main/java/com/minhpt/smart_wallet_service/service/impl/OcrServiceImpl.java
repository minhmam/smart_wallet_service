package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.constant.Constant;
import com.minhpt.smart_wallet_service.dto.response.OcrExtractResponse;
import com.minhpt.smart_wallet_service.model.Category;
import com.minhpt.smart_wallet_service.model.User;
import com.minhpt.smart_wallet_service.repository.CategoryRepository;
import com.minhpt.smart_wallet_service.service.OcrService;
import com.minhpt.smart_wallet_service.util.AuthenticationUtil;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    private final OcrTransactionAiExtractor ocrTransactionAiExtractor;
    private final CategoryRepository categoryRepository;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public String extractText(MultipartFile multipartFile) {
        File tempFile = null;
        try {
            validateImage(multipartFile);

            tempFile = File.createTempFile("ocr_", "_" + multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);

            ITesseract tesseract = new Tesseract();

            String tessDataPath = new ClassPathResource("tessdata").getFile().getAbsolutePath();
            tesseract.setDatapath(tessDataPath);

            // OCR tiếng Việt + tiếng Anh
            tesseract.setLanguage("vie+eng");

            // PSM 6 phù hợp cho block text thông thường
            tesseract.setPageSegMode(6);

            return tesseract.doOCR(tempFile).trim();

        } catch (TesseractException e) {
            throw new RuntimeException("OCR failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Cannot process OCR file", e);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Override
    public OcrExtractResponse extractTransactions(MultipartFile multipartFile) {
        String text = extractText(multipartFile);
        User currentUser = authenticationUtil.getCurrentUser();
        List<Category> categories = categoryRepository.findTop10ByStatusOrderByIdAsc(Constant.NOT_DELETE);

        String accountName = currentUser.getFullName();
        if (accountName == null || accountName.isBlank()) {
            accountName = currentUser.getUsername();
        }

        return OcrExtractResponse.builder()
                .text(text)
                .transactions(ocrTransactionAiExtractor.extractTransactions(text, accountName, categories))
                .build();
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are supported");
        }
    }
}
