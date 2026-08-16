package com.aydog4nn.manitimleproje.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email veya şifre hatalı.");
    }
}
