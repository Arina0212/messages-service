package ru.urfu.messages.web;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.urfu.messages.dto.ApiResponse;
import ru.urfu.messages.exception.ApiException;

@RestControllerAdvice
@Order(1)
public class DomainExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomain(ApiException exception) {
        return ResponseEntity.ok(ApiResponse.error(exception.code(), exception.getMessage()));
    }
}
