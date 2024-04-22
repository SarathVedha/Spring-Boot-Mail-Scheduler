package com.vedha.mail.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GlobalException {

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, String>> handleMailException(MailException mailException) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", mailException.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception exception) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", exception.getMessage()));
    }
}
