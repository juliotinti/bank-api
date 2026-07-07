package com.julio.bank.api.service;

import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.exception.InsufficientBalanceException;
import com.julio.bank.api.repository.BalanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BalanceService
{

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository)
    {
        this.balanceRepository = balanceRepository;
    }

    @Transactional(readOnly = true)
    public Balance getBalance(String id) {
        return balanceRepository.findById(id)
                .orElseThrow(() -> new BalanceNotFoundException(id));
    }

    @Transactional
    public void deleteAll() {
        balanceRepository.deleteAll();
    }

    @Transactional
    public Balance withdraw(String id, Long amount)
    {
        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BalanceNotFoundException(id));

        if (balance.getBalance() < amount)
        {
            throw new InsufficientBalanceException(id);
        }

        balance.withdraw(amount);
        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance credit(String id, Long amount)
    {
        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new BalanceNotFoundException(id));

        balance.deposit(amount);
        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance deposit(String id, Long amount)
    {
        return balanceRepository.findByIdForUpdate(id)
                .map(balance -> {
                    balance.deposit(amount);
                    return balanceRepository.save(balance);
                })
                .orElseGet(() -> balanceRepository.save(new Balance(id, amount)));
    }
}
