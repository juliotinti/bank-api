package com.julio.bank.api.controller;

import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.service.BalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService)
    {
        this.balanceService = balanceService;
    }

    @GetMapping("/balance")
    public ResponseEntity<Long> getBalance(@RequestParam("account_id") String accountId)
    {
        Balance balance = balanceService.getBalance(accountId);
        return ResponseEntity.ok(balance.getBalance());
    }
}
