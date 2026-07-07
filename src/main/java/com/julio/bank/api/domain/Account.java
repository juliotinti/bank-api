package com.julio.bank.api.domain;

import com.julio.bank.api.entity.Balance;

public record Account(String id, Long balance) {

    public static Account from(Balance balance) {
        return new Account(balance.getId(), balance.getBalance());
    }
}
