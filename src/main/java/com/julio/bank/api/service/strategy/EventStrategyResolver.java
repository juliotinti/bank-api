package com.julio.bank.api.service.strategy;

import com.julio.bank.api.domain.EventType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventStrategyResolver
{

    private final Map<EventType, EventStrategy> strategies;

    public EventStrategyResolver(List<EventStrategy> strategyList)
    {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(EventStrategy::supportedType, Function.identity()));
    }

    public EventStrategy resolve(EventType type)
    {
        EventStrategy strategy = strategies.get(type);
        if (strategy == null)
        {
            throw new IllegalArgumentException("Unsupported event type: " + type);
        }
        return strategy;
    }
}
