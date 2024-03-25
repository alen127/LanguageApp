package com.languageapp.exceptions;

import java.io.IOException;

public class FileEmptyException extends IOException {
    public FileEmptyException() {
    }

    public FileEmptyException(String message) {
        super(message);
    }

    public FileEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileEmptyException(Throwable cause) {
        super(cause);
    }
}
