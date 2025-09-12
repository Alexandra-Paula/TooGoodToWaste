package org.application.waste.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidFileException extends RuntimeException {
    private final HttpStatus status;

    public InvalidFileException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }
}