package com.julio.bank.api.service.strategy;

import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.domain.EventType;
import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.service.BalanceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositStrategyTest {

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private DepositStrategy depositStrategy;

    @Test
    void shouldReturnDepositType_whenSupportedTypeIsCalled_thenTypeMatchesDeposit()
    {
        assertThat(depositStrategy.supportedType()).isEqualTo(EventType.DEPOSIT);
    }

    @Test
    void shouldDepositIntoDestination_whenExecutingDepositEvent_thenResultContainsOnlyDestination() {
        when(balanceService.deposit("100", 10L)).thenReturn(new Balance("100", 10L));

        EventResult result = depositStrategy.execute(new EventRequest(EventType.DEPOSIT, null, "100", 10L));

        assertThat(result.origin()).isNull();
        assertThat(result.destination()).isNotNull();
        assertThat(result.destination().id()).isEqualTo("100");
        assertThat(result.destination().balance()).isEqualTo(10L);
        verify(balanceService, times(1)).deposit("100", 10L);
    }
}
