



package com.studenttap.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "*")
public class QRCodeController {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // ===================================================
    // GET /api/public/qr/{username}
    // Returns QR code image for student portfolio
    // No login needed - public access
    // ===================================================
    @GetMapping("/qr/{username}")
    public ResponseEntity<byte[]> generateQRCode(
            @PathVariable String username,
            @RequestParam(defaultValue = "250") int size)
            throws WriterException, IOException {

        // Portfolio URL that QR code will open
        String portfolioUrl = baseUrl
            + "/" + username;

        // Generate QR code
        QRCodeWriter qrWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION,
            ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix = qrWriter.encode(
            portfolioUrl,
            BarcodeFormat.QR_CODE,
            size, size, hints);

        // Convert to PNG image bytes
        ByteArrayOutputStream outputStream =
            new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(
            bitMatrix, "PNG", outputStream);

        byte[] qrBytes = outputStream.toByteArray();

        // Return as PNG image
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrBytes.length);
        headers.set("Cache-Control",
            "public, max-age=86400");

        return ResponseEntity.ok()
            .headers(headers)
            .body(qrBytes);
    }

    // ===================================================
    // GET /api/public/qr/{username}/url
    // Returns just the portfolio URL as text
    // ===================================================
    @GetMapping("/qr/{username}/url")
    public ResponseEntity<String> getPortfolioUrl(
            @PathVariable String username) {
        String url = baseUrl + "/" + username;
        return ResponseEntity.ok(url);
    }
}