package com.julio.bank.api.exception;

public class BalanceNotFoundException extends RuntimeException
{
    public BalanceNotFoundException(String accountId)
    {
        super("Account not found: " + accountId);
    }
}
