package com.julio.bank.api.exception;

public class InvalidAmountException extends RuntimeException
{
    public InvalidAmountException(Long amount)
    {
        super("Amount must be positive, got: " + amount);
    }
}