package com.redcare.githubscoring.exception;

public class RepositoryScoringException extends RuntimeException {
    public RepositoryScoringException(String message) {
        super(message);
    }

    public RepositoryScoringException(String message, Throwable cause) {
        super(message, cause);
    }
}
