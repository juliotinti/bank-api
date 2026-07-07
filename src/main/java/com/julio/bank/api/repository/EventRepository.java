package com.julio.bank.api.repository;

import com.julio.bank.api.entity.Event;

public interface EventRepository {
    Event save(Event event);
    void deleteAll();
}
