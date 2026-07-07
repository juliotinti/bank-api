package com.julio.bank.api.repository;

import com.julio.bank.api.entity.Balance;

import java.util.Optional;

public interface BalanceRepository {

    Optional<Balance> findById(String id);

    Optional<Balance> findByIdForUpdate(String id);

    Balance save(Balance balance);

    void deleteAll();
}

