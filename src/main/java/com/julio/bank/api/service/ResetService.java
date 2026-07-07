package com.julio.bank.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
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
        log.info("Resetting all data");
        eventService.deleteAll();
        balanceService.deleteAll();
    }
}