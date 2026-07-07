package com.julio.bank.api;

import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.domain.EventType;
import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.service.BalanceService;
import com.julio.bank.api.service.EventService;
import com.julio.bank.api.service.ResetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ApiFlowIntegrationTest
{

    @Autowired
    private ResetService resetService;

    @Autowired
    private EventService eventService;

    @Autowired
    private BalanceService balanceService;

    @Test
    void shouldProcessCompleteBankFlow_whenEventsAreExecutedInSequence_thenStateIsConsistent()
    {
        resetService.resetAll();

        EventResult initialDeposit = eventService.process(
                new EventRequest(EventType.DEPOSIT, null, "100", 10L));
        assertThat(initialDeposit.destination().id()).isEqualTo("100");
        assertThat(initialDeposit.destination().balance()).isEqualTo(10L);

        EventResult secondDeposit = eventService.process(
                new EventRequest(EventType.DEPOSIT, null, "100", 10L));
        assertThat(secondDeposit.destination().id()).isEqualTo("100");
        assertThat(secondDeposit.destination().balance()).isEqualTo(20L);

        Balance balanceAfterDeposits = balanceService.getBalance("100");
        assertThat(balanceAfterDeposits.getBalance()).isEqualTo(20L);

        assertThatThrownBy(() -> eventService.process(
                new EventRequest(EventType.WITHDRAW, "200", null, 10L)))
                .isInstanceOf(BalanceNotFoundException.class);

        EventResult withdraw = eventService.process(
                new EventRequest(EventType.WITHDRAW, "100", null, 5L));
        assertThat(withdraw.origin().id()).isEqualTo("100");
        assertThat(withdraw.origin().balance()).isEqualTo(15L);

        EventResult transfer = eventService.process(
                new EventRequest(EventType.TRANSFER, "100", "300", 15L));
        assertThat(transfer.origin().id()).isEqualTo("100");
        assertThat(transfer.origin().balance()).isEqualTo(0L);
        assertThat(transfer.destination().id()).isEqualTo("300");
        assertThat(transfer.destination().balance()).isEqualTo(15L);

        assertThatThrownBy(() -> eventService.process(
                new EventRequest(EventType.TRANSFER, "200", "300", 15L)))
                .isInstanceOf(BalanceNotFoundException.class);

        assertThatThrownBy(() -> balanceService.getBalance("1234"))
                .isInstanceOf(BalanceNotFoundException.class);

        assertThat(balanceService.getBalance("100").getBalance()).isEqualTo(0L);
        assertThat(balanceService.getBalance("300").getBalance()).isEqualTo(15L);
    }
}
