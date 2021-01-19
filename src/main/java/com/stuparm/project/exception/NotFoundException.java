package com.stuparm.project.exception;

public class NotFoundException extends RuntimeException{

    public NotFoundException(String message) {
        super(message);
    }

    public static final String GATE_NOT_FOUND_FORMAT = "Gate %s not found";
}
