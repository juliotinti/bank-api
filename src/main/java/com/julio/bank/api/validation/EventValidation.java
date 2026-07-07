package com.julio.bank.api.validation;

import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventType;
import com.julio.bank.api.exception.InvalidAmountException;
import com.julio.bank.api.exception.InvalidEventTypeException;
import org.springframework.stereotype.Component;

@Component
public class EventValidation
{
    public EventRequest validate(String rawType, String origin, String destination, Long amount) {
        validateAmount(amount);
        EventType type = parseType(rawType);
        return new EventRequest(type, origin, destination, amount);
    }

    private void validateAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new InvalidAmountException(amount);
        }
    }

    private EventType parseType(String rawType) {
        if (rawType == null) {
            throw new InvalidEventTypeException(null);
        }
        try {
            return EventType.valueOf(rawType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidEventTypeException(rawType);
        }
    }
}