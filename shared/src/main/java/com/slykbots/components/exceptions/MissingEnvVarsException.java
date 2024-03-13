package com.slykbots.components.exceptions;

public class MissingEnvVarsException extends RuntimeException {
    public MissingEnvVarsException(String message) {
        super(message);
    }
}
