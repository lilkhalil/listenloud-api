package com.lilkhalil.listenloud.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.lilkhalil.listenloud.model.ExceptionResponse;

@RestControllerAdvice
public class FileUploadExceptionAdvice {
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException e) {
        return new ResponseEntity<String>("File size exceeds the allowed limits! (10 MB)", HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(NotValidContentTypeException.class)
    public ResponseEntity<?> handleNotValidContentTypeException(NotValidContentTypeException e) {
        return new ResponseEntity<>(
            ExceptionResponse.builder()
                .code(HttpStatus.UNSUPPORTED_MEDIA_TYPE.name())
                .timestamp(LocalDateTime.now().toString())
                .message(e.getMessage())
                .build(), 
            HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

}
