package com.example.project_back.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
}
