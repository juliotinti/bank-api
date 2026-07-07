package com.julio.bank.api.domain;

public record EventResult(Account origin, Account destination) {

    public static EventResult of(Account origin, Account destination) {
        return new EventResult(origin, destination);
    }
}
