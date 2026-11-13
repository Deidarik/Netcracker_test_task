package com.example.lmslite.exception;

public class AlreadyEnrolledException extends RuntimeException {

    public AlreadyEnrolledException(String message) {
        super(message);
    }
}