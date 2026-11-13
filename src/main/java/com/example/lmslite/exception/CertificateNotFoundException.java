package com.example.lmslite.exception;

public class CertificateNotFoundException extends RuntimeException {

    public CertificateNotFoundException(String message) {
        super(message);
    }
}