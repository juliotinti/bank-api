package com.julio.bank.api.exception;

public class InvalidEventTypeException extends RuntimeException
{
    public InvalidEventTypeException(String type)
    {
        super(type == null || type.isBlank() ? "Event type is required" : "Unsupported event type: " + type);
    }
}
