package com.julio.bank.api.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventResult(Account origin, Account destination) {

    public static EventResult of(Account origin, Account destination) {
        return new EventResult(origin, destination);
    }
}
