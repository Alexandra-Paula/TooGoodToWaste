package org.application.waste.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RoleNotAllowedException extends RuntimeException {
    private final HttpStatus status;

    public RoleNotAllowedException(String message) {
        super(message);
        this.status = HttpStatus.FORBIDDEN;
    }
}