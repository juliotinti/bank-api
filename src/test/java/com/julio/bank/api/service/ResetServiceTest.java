package com.julio.bank.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ResetServiceTest
{

    @Mock
    private BalanceService balanceService;

    @Mock
    private EventService eventService;

    @InjectMocks
    private ResetService resetService;

    @Test
    void shouldDeleteAllEventsAndBalances_whenResetAllIsCalled_thenBothRepositoriesAreCleared()
    {
        resetService.resetAll();

        verify(eventService, times(1)).deleteAll();
        verify(balanceService, times(1)).deleteAll();
    }

    @Test
    void shouldDeleteEventsBeforeBalances_whenResetAllIsCalled_thenOrderIsRespected()
    {
        resetService.resetAll();

        InOrder inOrder = Mockito.inOrder(eventService, balanceService);
        inOrder.verify(eventService).deleteAll();
        inOrder.verify(balanceService).deleteAll();
    }
}