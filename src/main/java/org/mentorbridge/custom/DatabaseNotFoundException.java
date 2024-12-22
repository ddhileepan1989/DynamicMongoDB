package org.mentorbridge.custom;

public class DatabaseNotFoundException extends RuntimeException {
    public DatabaseNotFoundException(String message) {
        super(message);
    }

    // You can also add additional constructors if needed
    public DatabaseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

