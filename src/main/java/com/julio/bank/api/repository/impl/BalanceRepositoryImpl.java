package com.julio.bank.api.repository.impl;

import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.repository.BalanceRepository;
import com.julio.bank.api.repository.jpa.BalanceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class BalanceRepositoryImpl implements BalanceRepository {

    private final BalanceJpaRepository jpaRepository;

    public BalanceRepositoryImpl(BalanceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Balance> findById(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Balance save(Balance balance) {
        return jpaRepository.save(balance);
    }

    @Override
    public int addToBalance(String id, Long delta) {
        return jpaRepository.addToBalance(id, delta);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
