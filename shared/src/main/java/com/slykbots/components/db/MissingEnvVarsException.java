package com.slykbots.components.db;

public class MissingEnvVarsException extends RuntimeException {
    public MissingEnvVarsException(String message) {
        super(message);
    }
}
