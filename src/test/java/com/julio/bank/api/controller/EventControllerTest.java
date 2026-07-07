package com.julio.bank.api.controller;

import com.julio.bank.api.domain.Account;
import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.exception.InsufficientBalanceException;
import com.julio.bank.api.service.EventService;
import com.julio.bank.api.validation.EventValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@Import(EventValidation.class)
class EventControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void shouldReturn201WithDestinationOnly_whenDepositSucceeds_thenBodyMatchesContract()
    {
        when(eventService.process(any(EventRequest.class)))
                .thenReturn(EventResult.of(null, new Account("100", 10L)));

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100"))
                .andExpect(jsonPath("$.destination.balance").value(10))
                .andExpect(jsonPath("$.origin").doesNotExist()));
    }

    @Test
    void shouldReturn201WithOriginOnly_whenWithdrawSucceeds_thenBodyMatchesContract()
    {
        when(eventService.process(any(EventRequest.class)))
                .thenReturn(EventResult.of(new Account("100", 15L), null));

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"withdraw\",\"origin\":\"100\",\"amount\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.id").value("100"))
                .andExpect(jsonPath("$.origin.balance").value(15))
                .andExpect(jsonPath("$.destination").doesNotExist()));
    }

    @Test
    void shouldReturn201WithBothAccounts_whenTransferSucceeds_thenBodyMatchesContract()
    {
        when(eventService.process(any(EventRequest.class)))
                .thenReturn(EventResult.of(new Account("100", 0L), new Account("300", 15L)));

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"transfer\",\"origin\":\"100\",\"destination\":\"300\",\"amount\":15}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.origin.balance").value(0))
                .andExpect(jsonPath("$.destination.balance").value(15)));
    }

    @Test
    void shouldReturn404WithErrorBody_whenAccountNotFound_thenMatchesContract()
    {
        when(eventService.process(any(EventRequest.class)))
                .thenThrow(new BalanceNotFoundException("200"));

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"withdraw\",\"origin\":\"200\",\"amount\":10}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Account not found: 200"))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn400WithErrorBody_whenBalanceIsInsufficient_thenMatchesConvention() {
        when(eventService.process(any(EventRequest.class)))
                .thenThrow(new InsufficientBalanceException("100"));

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"withdraw\",\"origin\":\"100\",\"amount\":9999}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Insufficient balance for account: 100"))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn400WithErrorBody_whenAmountIsNegative_thenEventServiceIsNeverCalled()
    {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":-10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be positive, got: -10"))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn400WithErrorBody_whenEventTypeIsUnknown_thenEventServiceIsNeverCalled()
    {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"unknown\",\"destination\":\"100\",\"amount\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Unsupported event type: unknown"))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn400WithErrorBody_whenAmountIsMissing_thenEventServiceIsNeverCalled()
    {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\",\"destination\":\"100\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be positive, got: null"))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn400WithErrorBody_whenAmountIsZero_thenEventServiceIsNeverCalled()
    {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"deposit\",\"destination\":\"100\",\"amount\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be positive, got: 0"))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn400WithErrorBody_whenTypeIsMissing_thenEventServiceIsNeverCalled()
    {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destination\":\"100\",\"amount\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Unsupported event type: null"))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn400WithErrorBody_whenTypeIsEmptyString_thenEventServiceIsNeverCalled()
    {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"\",\"destination\":\"100\",\"amount\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Unsupported event type: "))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn400WithErrorBody_whenBothTypeAndAmountAreInvalid_thenEventServiceIsNeverCalled()
    {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"unknown\",\"destination\":\"100\",\"amount\":-10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Amount must be positive, got: -10"))
                .andExpect(jsonPath("$.path").value("/event")));
    }

    @Test
    void shouldReturn201_whenTypeHasMixedCasing_thenTypeIsParsedCorrectly()
    {
        when(eventService.process(any(EventRequest.class)))
                .thenReturn(EventResult.of(null, new Account("100", 10L)));

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"DePosIt\",\"destination\":\"100\",\"amount\":10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.destination.id").value("100")));
    }
}
