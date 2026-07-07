package com.julio.bank.api.service.strategy;

import com.julio.bank.api.domain.Account;
import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.domain.EventType;
import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.service.BalanceService;
import org.springframework.stereotype.Component;

@Component
public class TransferStrategy implements EventStrategy
{

    private final BalanceService balanceService;

    public TransferStrategy(BalanceService balanceService)
    {
        this.balanceService = balanceService;
    }

    @Override
    public EventType supportedType()
    {
        return EventType.TRANSFER;
    }

    @Override
    public EventResult execute(EventRequest request)
    {
        Balance origin = balanceService.withdraw(request.origin(), request.amount());
        Balance destination = balanceService.credit(request.destination(), request.amount());
        return EventResult.of(Account.from(origin), Account.from(destination));
    }
}
