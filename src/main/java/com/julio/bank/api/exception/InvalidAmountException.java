package com.julio.bank.api.exception;

public class InvalidAmountException extends RuntimeException
{
    public InvalidAmountException(Long amount)
    {
        super(amount == null ? "Amount is required" : "Amount must be positive: " + amount);
    }
}
