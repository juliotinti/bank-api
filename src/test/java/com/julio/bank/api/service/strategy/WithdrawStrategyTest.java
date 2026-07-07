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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawStrategyTest {

    @Mock
    private BalanceService balanceService;

    @InjectMocks
    private WithdrawStrategy withdrawStrategy;

    @Test
    void shouldReturnWithdrawType_whenSupportedTypeIsCalled_thenTypeMatchesWithdraw()
    {
        assertThat(withdrawStrategy.supportedType()).isEqualTo(EventType.WITHDRAW);
    }

    @Test
    void shouldWithdrawFromOrigin_whenExecutingWithdrawEvent_thenResultContainsOnlyOrigin()
    {
        when(balanceService.withdraw("100", 5L)).thenReturn(new Balance("100", 15L));

        EventResult result = withdrawStrategy.execute(new EventRequest(EventType.WITHDRAW, "100", null, 5L));

        assertThat(result.destination()).isNull();
        assertThat(result.origin()).isNotNull();
        assertThat(result.origin().id()).isEqualTo("100");
        assertThat(result.origin().balance()).isEqualTo(15L);
    }

    @Test
    void shouldPropagateBalanceNotFoundException_whenOriginDoesNotExist_thenThrows()
    {
        when(balanceService.withdraw("200", 10L)).thenThrow(new BalanceNotFoundException("200"));

        assertThatThrownBy(() -> withdrawStrategy.execute(new EventRequest(EventType.WITHDRAW, "200", null, 10L)))
                .isInstanceOf(BalanceNotFoundException.class);
    }

    @Test
    void shouldPropagateInsufficientBalanceException_whenBalanceServiceThrowsIt_thenExecuteThrows()
    {
        when(balanceService.withdraw("100", 100L)).thenThrow(new InsufficientBalanceException("100"));

        assertThatThrownBy(() -> withdrawStrategy.execute(new EventRequest(EventType.WITHDRAW, "100", null, 100L)))
                .isInstanceOf(InsufficientBalanceException.class);
    }
}
