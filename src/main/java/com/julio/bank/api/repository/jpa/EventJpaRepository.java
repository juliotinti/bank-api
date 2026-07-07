package com.julio.bank.api.repository.jpa;

import com.julio.bank.api.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<Event, Long> {
}
