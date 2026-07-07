package com.julio.bank.api.controller;

import com.julio.bank.api.controller.dto.EventRequestDto;
import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.domain.EventType;
import com.julio.bank.api.service.EventService;
import com.julio.bank.api.validation.EventValidation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController
{

    private final EventService eventService;

    private final EventValidation eventValidation;

    public EventController(EventService eventService, EventValidation eventValidation)
    {
        this.eventService = eventService;
        this.eventValidation = eventValidation;
    }

    @PostMapping
    public ResponseEntity<EventResult> handle(@RequestBody EventRequestDto dto)
    {
        EventRequest request = eventValidation.validate(
                dto.type(), dto.origin(), dto.destination(), dto.amount());

        EventResult result = eventService.process(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    private EventType parseType(String rawType)
    {
        if (rawType == null)
        {
            throw new IllegalArgumentException("Event type must not be null");
        }
        try
        {
            return EventType.valueOf(rawType.toUpperCase());
        } catch (IllegalArgumentException ex)
        {
            throw new IllegalArgumentException("Unsupported event type: " + rawType);
        }
    }
}
