
package com.example.lmslite.controller;

import com.example.lmslite.dto.CertificateResponse;
import com.example.lmslite.service.CertificateService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping("/{code}")
    public CertificateResponse verifyCertificate(
            @PathVariable String code
    ) {
        return certificateService.verifyCertificate(code);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadCertificate(
            @PathVariable Long id
    ) {

        String content =
                certificateService.generateCertificateText(id);

        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                "text/plain; charset=UTF-8"
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"certificate-" + id + ".txt\""
                )
                .body(bytes);
    }
}
