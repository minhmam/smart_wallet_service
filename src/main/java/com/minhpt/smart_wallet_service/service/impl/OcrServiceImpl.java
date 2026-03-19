package com.minhpt.smart_wallet_service.service.impl;

import com.minhpt.smart_wallet_service.service.OcrService;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class OcrServiceImpl implements OcrService {
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
