package com.julio.bank.api.controller;

import com.julio.bank.api.controller.dto.ApiErrorResponse;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.exception.InsufficientBalanceException;
import com.julio.bank.api.exception.InvalidAmountException;
import com.julio.bank.api.exception.InvalidEventTypeException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBalanceNotFound(BalanceNotFoundException ex, HttpServletRequest request)
    {
        log.warn("Returning 404 for balance not found: {}", ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex, HttpServletRequest request)
    {
        log.warn("Returning 400 for insufficient balance: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidAmount(InvalidAmountException ex, HttpServletRequest request)
    {
        log.warn("Returning 400 for invalid amount: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request)
    {
        log.warn("Returning 400 for invalid request: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(InvalidEventTypeException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidEventType(InvalidEventTypeException ex, HttpServletRequest request) {
        log.warn("Returning 400 for invalid event type: {}", ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex, request);
    }

    private ResponseEntity<ApiErrorResponse> buildError(HttpStatus status, RuntimeException ex, HttpServletRequest request)
    {
        ApiErrorResponse response = new ApiErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(status).body(response);
    }
}
