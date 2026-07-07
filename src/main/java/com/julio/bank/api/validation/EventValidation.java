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
        validateRequiredFields(type, origin, destination);
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

    private void validateRequiredFields(EventType type, String origin, String destination) {
        switch (type) {
            case DEPOSIT -> requirePresent(destination, "destination");
            case WITHDRAW -> requirePresent(origin, "origin");
            case TRANSFER -> {
                requirePresent(origin, "origin");
                requirePresent(destination, "destination");
            }
        }
    }

    private void requirePresent(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }
}
