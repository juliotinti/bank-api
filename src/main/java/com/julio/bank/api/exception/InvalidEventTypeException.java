package com.julio.bank.api.exception;

public class InvalidEventTypeException extends RuntimeException
{
    public InvalidEventTypeException(String type)
    {
        super("Unsupported event type: " + type);
    }
}