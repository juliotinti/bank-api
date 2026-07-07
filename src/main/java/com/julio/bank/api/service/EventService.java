package com.julio.bank.api.service;

import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.entity.Event;
import com.julio.bank.api.repository.EventRepository;
import com.julio.bank.api.service.strategy.EventStrategy;
import com.julio.bank.api.service.strategy.EventStrategyResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService
{

    private final EventRepository eventRepository;
    private final EventStrategyResolver strategyResolver;

    public EventService(EventRepository eventRepository, EventStrategyResolver strategyResolver)
    {
        this.eventRepository = eventRepository;
        this.strategyResolver = strategyResolver;
    }

    @Transactional
    public Event save(Event event)
    {
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteAll()
    {
        eventRepository.deleteAll();
    }

    @Transactional
    public EventResult process(EventRequest request)
    {
        EventStrategy strategy = strategyResolver.resolve(request.type());
        EventResult result = strategy.execute(request);

        eventRepository.save(
                new Event(request.type().name().toLowerCase(),
                          request.origin(), request.destination(),
                          request.amount()
        ));

        return result;
    }
}
