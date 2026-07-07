package com.julio.bank.api.controller;

import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.exception.InsufficientBalanceException;
import com.julio.bank.api.exception.InvalidAmountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(BalanceNotFoundException.class)
    public ResponseEntity<Integer> handleBalanceNotFound(BalanceNotFoundException ex)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Integer> handleInsufficientBalance(InsufficientBalanceException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<Integer> handleInvalidAmount(InvalidAmountException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Integer> handleIllegalArgument(IllegalArgumentException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
    }
}
