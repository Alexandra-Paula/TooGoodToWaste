package org.application.waste.config;

import org.application.waste.exceptions.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerConfig {
    @ExceptionHandler(NotFoundException.class)
    private ResponseEntity<Object> notFoundException(NotFoundException notFoundException) {
        Map<String, String> map = new HashMap<>();

        map.put("timestamp", Instant.now().toString());
        map.put("message", notFoundException.getMessage());
        map.put("status", notFoundException.getStatus().toString());

        return ResponseEntity.status(notFoundException.getStatus()).body(map);
    }

    @ExceptionHandler(AlreadyExistException.class)
    private ResponseEntity<Object> alreadyExistException(AlreadyExistException alreadyExistException) {
        Map<String, String> map = new HashMap<>();

        map.put("timestamp", Instant.now().toString());
        map.put("message", alreadyExistException.getMessage());
        map.put("status", alreadyExistException.getStatus().toString());

        return ResponseEntity.status(alreadyExistException.getStatus()).body(map);
    }

    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<Object> badRequestException(BadRequestException badRequestException) {
        Map<String, String> map = new HashMap<>();

        map.put("timestamp", Instant.now().toString());
        map.put("message", badRequestException.getMessage());
        map.put("status", badRequestException.getStatus().toString());

        return ResponseEntity.status(badRequestException.getStatus()).body(map);
    }

    @ExceptionHandler(FileTooLargeException.class)
    private ResponseEntity<Object> fileTooLargeException(FileTooLargeException fileTooLargeException) {
        Map<String, String> map = new HashMap<>();

        map.put("timestamp", Instant.now().toString());
        map.put("message", fileTooLargeException.getMessage());
        map.put("status", fileTooLargeException.getStatus().toString());

        return ResponseEntity.status(fileTooLargeException.getStatus()).body(map);
    }

    @ExceptionHandler(ForbiddenException.class)
    private ResponseEntity<Object> forbiddenException(ForbiddenException forbiddenException) {
        Map<String, String> map = new HashMap<>();

        map.put("timestamp", Instant.now().toString());
        map.put("message", forbiddenException.getMessage());
        map.put("status", forbiddenException.getStatus().toString());

        return ResponseEntity.status(forbiddenException.getStatus()).body(map);
    }

    @ExceptionHandler(RoleNotAllowedException.class)
    private ResponseEntity<Object> roleNotAllowedException(RoleNotAllowedException roleNotAllowedException) {
        Map<String, String> map = new HashMap<>();

        map.put("timestamp", Instant.now().toString());
        map.put("message", roleNotAllowedException.getMessage());
        map.put("status", roleNotAllowedException.getStatus().toString());

        return ResponseEntity.status(roleNotAllowedException.getStatus()).body(map);
    }

    @ExceptionHandler(UnauthorizedException.class)
    private ResponseEntity<Object> unauthorizedException(UnauthorizedException unauthorizedException) {
        Map<String, String> map = new HashMap<>();

        map.put("timestamp", Instant.now().toString());
        map.put("message", unauthorizedException.getMessage());
        map.put("status", unauthorizedException.getStatus().toString());

        return ResponseEntity.status(unauthorizedException.getStatus()).body(map);
    }
}