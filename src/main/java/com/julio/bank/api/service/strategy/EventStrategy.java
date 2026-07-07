package com.julio.bank.api.service.strategy;

import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.domain.EventType;

public interface EventStrategy
{
    EventType supportedType();

    EventResult execute(EventRequest request);
}
