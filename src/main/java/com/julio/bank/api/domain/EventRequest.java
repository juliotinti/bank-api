package com.julio.bank.api.domain;

import com.julio.bank.api.exception.InvalidAmountException;

public record EventRequest(EventType type, String origin, String destination, Long amount) {
    public EventRequest
    {
        if (amount == null || amount <= 0)
        {
            throw new InvalidAmountException(amount);
        }
    }
}
