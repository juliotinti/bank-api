package com.julio.bank.api.service.strategy;

import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.domain.EventType;
import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.exception.InsufficientBalanceException;
import com.julio.bank.api.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferStrategyTest {

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private TransferStrategy transferStrategy;

    @Test
    void shouldReturnTransferType_whenSupportedTypeIsCalled_thenTypeMatchesTransfer()
    {
        assertThat(transferStrategy.supportedType()).isEqualTo(EventType.TRANSFER);
    }

    @Test
    void shouldTransferBetweenAccounts_whenBothExist_thenResultContainsOriginAndDestination()
    {
        when(balanceService.withdraw("100", 15L)).thenReturn(new Balance("100", 0L));
        when(balanceService.credit("300", 15L)).thenReturn(new Balance("300", 15L));

        EventResult result = transferStrategy.execute(new EventRequest(EventType.TRANSFER, "100", "300", 15L));

        assertThat(result.origin().id()).isEqualTo("100");
        assertThat(result.origin().balance()).isEqualTo(0L);
        assertThat(result.destination().id()).isEqualTo("300");
        assertThat(result.destination().balance()).isEqualTo(15L);
    }

    @Test
    void shouldPropagateBalanceNotFoundException_whenOriginDoesNotExist_thenCreditIsNeverCalled()
    {
        when(balanceService.withdraw("200", 15L)).thenThrow(new BalanceNotFoundException("200"));

        assertThatThrownBy(() -> transferStrategy.execute(new EventRequest(EventType.TRANSFER, "200", "300", 15L)))
                .isInstanceOf(BalanceNotFoundException.class);

        verify(balanceService, never()).credit(any(), any());
    }

    @Test
    void shouldPropagateBalanceNotFoundException_whenDestinationDoesNotExist_thenExecuteThrows()
    {
        when(balanceService.withdraw("100", 15L)).thenReturn(new Balance("100", 0L));
        when(balanceService.credit("999", 15L)).thenThrow(new BalanceNotFoundException("999"));

        assertThatThrownBy(() -> transferStrategy.execute(new EventRequest(EventType.TRANSFER, "100", "999", 15L)))
                .isInstanceOf(BalanceNotFoundException.class);
    }

    @Test
    void shouldPropagateInsufficientBalanceException_whenOriginHasInsufficientBalance_thenCreditIsNeverCalled()
    {
        when(balanceService.withdraw("100", 999L)).thenThrow(new InsufficientBalanceException("100"));

        assertThatThrownBy(() -> transferStrategy.execute(new EventRequest(EventType.TRANSFER, "100", "300", 999L)))
                .isInstanceOf(InsufficientBalanceException.class);

        verify(balanceService, never()).credit(any(), any());
    }
}
