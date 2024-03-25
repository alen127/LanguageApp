package com.languageapp.exceptions;

public class MultipleHighestScoringUsersException extends RuntimeException {
    public MultipleHighestScoringUsersException() {
    }

    public MultipleHighestScoringUsersException(String message) {
        super(message);
    }

    public MultipleHighestScoringUsersException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleHighestScoringUsersException(Throwable cause) {
        super(cause);
    }

    public MultipleHighestScoringUsersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
