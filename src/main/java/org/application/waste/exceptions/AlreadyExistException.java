package org.application.waste.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlreadyExistException extends RuntimeException {
    private final HttpStatus status;

    public AlreadyExistException(String message) {
        super(message);
        this.status = HttpStatus.CONFLICT;
    }
}