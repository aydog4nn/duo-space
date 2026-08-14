package com.aydog4nn.manitimleproje.exception;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String field) {
        super("A user already exists with this " + field + ".");
    }
}
