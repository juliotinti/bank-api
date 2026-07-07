package com.julio.bank.api.service;

import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.exception.InsufficientBalanceException;
import com.julio.bank.api.repository.BalanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceService balanceService;

    @Test
    void shouldReturnBalance_whenAccountExists_thenBalanceValueIsCorrect()
    {
        when(balanceRepository.findById("100")).thenReturn(Optional.of(new Balance("100", 20L)));

        Balance balance = balanceService.getBalance("100");

        assertThat(balance.getBalance()).isEqualTo(20L);
    }

    @Test
    void shouldThrowBalanceNotFoundException_whenGettingBalanceOfMissingAccount_thenNoBalanceIsReturned()
    {
        when(balanceRepository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.getBalance("999"))
                .isInstanceOf(BalanceNotFoundException.class);
    }

    @Test
    void shouldDeleteAllBalances_whenDeleteAllIsCalled_thenRepositoryDeleteAllIsInvoked() {
        balanceService.deleteAll();
        verify(balanceRepository, times(1)).deleteAll();
    }

    @Test
    void shouldWithdraw_whenBalanceIsSufficient_thenBalanceIsDecreasedAndSaved()
    {
        Balance balance = new Balance("100", 20L);
        when(balanceRepository.findByIdForUpdate("100")).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.withdraw("100", 5L);

        assertThat(result.getBalance()).isEqualTo(15L);
        verify(balanceRepository, times(1)).save(balance);
    }

    @Test
    void shouldThrowBalanceNotFoundException_whenWithdrawingFromMissingAccount_thenNothingIsSaved()
    {
        when(balanceRepository.findByIdForUpdate("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.withdraw("999", 10L))
                .isInstanceOf(BalanceNotFoundException.class);

        verify(balanceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldThrowInsufficientBalanceException_whenWithdrawAmountExceedsBalance_thenNothingIsSaved()
    {
        when(balanceRepository.findByIdForUpdate("100")).thenReturn(Optional.of(new Balance("100", 10L)));

        assertThatThrownBy(() -> balanceService.withdraw("100", 50L))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(balanceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldCreditAccount_whenAccountExists_thenBalanceIsIncreasedAndSaved()
    {
        Balance balance = new Balance("300", 10L);
        when(balanceRepository.findByIdForUpdate("300")).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.credit("300", 5L);

        assertThat(result.getBalance()).isEqualTo(15L);
    }

    @Test
    void shouldThrowBalanceNotFoundException_whenCreditingMissingAccount_thenNothingIsSaved() {
        when(balanceRepository.findByIdForUpdate("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.credit("999", 10L))
                .isInstanceOf(BalanceNotFoundException.class);

        verify(balanceRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldCreateAccount_whenDepositingIntoMissingAccount_thenNewBalanceIsPersisted()
    {
        when(balanceRepository.findByIdForUpdate("100")).thenReturn(Optional.empty());
        when(balanceRepository.save(org.mockito.ArgumentMatchers.any(Balance.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Balance result = balanceService.deposit("100", 10L);

        assertThat(result.getId()).isEqualTo("100");
        assertThat(result.getBalance()).isEqualTo(10L);
    }

    @Test
    void shouldIncrementBalance_whenDepositingIntoExistingAccount_thenSavedBalanceIsUpdated()
    {
        Balance balance = new Balance("100", 10L);
        when(balanceRepository.findByIdForUpdate("100")).thenReturn(Optional.of(balance));
        when(balanceRepository.save(balance)).thenReturn(balance);

        Balance result = balanceService.deposit("100", 10L);

        assertThat(result.getBalance()).isEqualTo(20L);
    }
}
