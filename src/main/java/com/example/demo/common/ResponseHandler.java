package com.example.demo.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ResponseHandler {
    public ResponseEntity<ResultResponse<?>> buildErrorResponse(HttpStatus status, String message) {
        ResultResponse<?> response = ResultResponse.builder()
                .message(message)
                .statusCode(status.value())
                .status(status)
                .timeStamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(status).body(response);
    }

    public <T> ResponseEntity<ResultResponse<T>> buildSuccessResponse(T body, String message, HttpStatus status) {
        ResultResponse<T> response = ResultResponse.<T>builder()
                .body(body)
                .message(message)
                .statusCode(status.value())
                .status(status)
                .timeStamp(LocalDateTime.now().toString())
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
