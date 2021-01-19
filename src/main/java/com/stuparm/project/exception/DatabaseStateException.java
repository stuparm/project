package com.stuparm.project.exception;

public class DatabaseStateException extends RuntimeException{

    public DatabaseStateException(String message) {
        super(message);
    }

    public DatabaseStateException() {
        super("State of the database is not as expected");
    }

    public static final String STATUS_DOESNT_EXIST_FORMAT = "Schedule/Status doesn't exist for gate %s at time %s";
}
