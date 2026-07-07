package com.julio.bank.api.domain;

public record EventRequest(EventType type, String origin, String destination, Long amount) { }
