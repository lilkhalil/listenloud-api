package com.lilkhalil.listenloud.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.lilkhalil.listenloud.model.ExceptionResponse;

@RestControllerAdvice
public class TagExceptionAdvice {
    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<?> handleBadCredentialsException(TagNotFoundException e) {
        return new ResponseEntity<>(
            ExceptionResponse.builder()
                .code(HttpStatus.NOT_FOUND.name())
                .timestamp(LocalDateTime.now().toString())
                .message(e.getMessage())
                .build(), 
            HttpStatus.NOT_FOUND);
    }
}
