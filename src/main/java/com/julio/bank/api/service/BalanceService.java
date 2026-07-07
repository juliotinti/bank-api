package com.julio.bank.api.service;

import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.exception.InsufficientBalanceException;
import com.julio.bank.api.repository.BalanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class BalanceService
{

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository)
    {
        this.balanceRepository = balanceRepository;
    }

    @Transactional(readOnly = true)
    public Balance getBalance(String id)
    {
        return balanceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Balance lookup failed, account not found: id={}", id);
                    return new BalanceNotFoundException(id);
                });
    }

    @Transactional
    public void deleteAll()
    {
        log.info("Resetting all balances");
        balanceRepository.deleteAll();
    }

    @Transactional
    public Balance withdraw(String id, Long amount)
    {
        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> {
                    log.error("Withdraw failed, account not found: id={}", id);
                    return new BalanceNotFoundException(id);
                });

        if (balance.getBalance() < amount)
        {
            log.error("Withdraw failed, insufficient balance: id={}, currentBalance={}, requestedAmount={}",
                    id, balance.getBalance(), amount);
            throw new InsufficientBalanceException(id);
        }

        balance.withdraw(amount);
        Balance saved = balanceRepository.save(balance);
        log.info("Withdraw succeeded: id={}, amount={}, newBalance={}", id, amount, saved.getBalance());
        return saved;
    }

    @Transactional
    public Balance credit(String id, Long amount)
    {
        Balance balance = balanceRepository.findByIdForUpdate(id)
                .orElseThrow(() -> {
                    log.error("Credit failed, account not found: id={}", id);
                    return new BalanceNotFoundException(id);
                });

        balance.deposit(amount);
        Balance saved = balanceRepository.save(balance);
        log.info("Credit succeeded: id={}, amount={}, newBalance={}", id, amount, saved.getBalance());
        return saved;
    }

    @Transactional
    public Balance deposit(String id, Long amount)
    {
        return balanceRepository.findByIdForUpdate(id)
                .map(balance -> {
                    balance.deposit(amount);
                    Balance saved = balanceRepository.save(balance);
                    log.info("Deposit succeeded (existing account): id={}, amount={}, newBalance={}",
                            id, amount, saved.getBalance());
                    return saved;
                })
                .orElseGet(() -> {
                    Balance saved = balanceRepository.save(new Balance(id, amount));
                    log.info("Deposit succeeded (account created): id={}, initialBalance={}", id, amount);
                    return saved;
                });
    }
}
