package com.julio.bank.api.repository.impl;

import com.julio.bank.api.entity.Event;
import com.julio.bank.api.repository.EventRepository;
import com.julio.bank.api.repository.jpa.EventJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository jpaRepository;

    public EventRepositoryImpl(EventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Event save(Event event) {
        return jpaRepository.save(event);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
