package com.julio.bank.api.controller;

import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.exception.InsufficientBalanceException;
import com.julio.bank.api.exception.InvalidAmountException;
import com.julio.bank.api.exception.InvalidEventTypeException;
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
    public ResponseEntity<Integer> handleBalanceNotFound(BalanceNotFoundException ex)
    {
        log.warn("Returning 404 for balance not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Integer> handleInsufficientBalance(InsufficientBalanceException ex)
    {
        log.warn("Returning 400 for insufficient balance: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<Integer> handleInvalidAmount(InvalidAmountException ex)
    {
        log.warn("Returning 400 for invalid amount: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Integer> handleIllegalArgument(IllegalArgumentException ex)
    {
        log.warn("Returning 400 for invalid request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }

    @ExceptionHandler(InvalidEventTypeException.class)
    public ResponseEntity<Integer> handleInvalidEventType(InvalidEventTypeException ex) {
        log.warn("Returning 400 for invalid event type: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }
}
