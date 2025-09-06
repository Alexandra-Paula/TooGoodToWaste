package org.application.waste.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileTooLargeException extends RuntimeException {
    private final HttpStatus status;

    public FileTooLargeException(String message) {
        super(message);
        this.status = HttpStatus.PAYLOAD_TOO_LARGE;
    }
}