package com.julio.bank.api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResetService
{

    private final BalanceService balanceService;
    private final EventService eventService;

    public ResetService(BalanceService balanceService, EventService eventService)
    {
        this.balanceService = balanceService;
        this.eventService = eventService;
    }

    @Transactional
    public void resetAll()
    {
        eventService.deleteAll();
        balanceService.deleteAll();
    }
}