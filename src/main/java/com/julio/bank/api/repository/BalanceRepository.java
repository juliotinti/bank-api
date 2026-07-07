package com.julio.bank.api.repository;

import com.julio.bank.api.entity.Balance;

import java.util.Optional;

public interface BalanceRepository {

    Optional<Balance> findById(String id);

    Balance save(Balance balance);

    /**
     * Directly using the database (UPDATE balances SET balance = balance + :delta WHERE id = :id).
     *
     * Returns the affects row quantity: 0 means that the count doesn't exists
     */
    int addToBalance(String id, Long delta);

    void deleteAll();
}

