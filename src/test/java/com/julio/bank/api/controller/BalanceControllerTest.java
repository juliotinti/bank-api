package com.julio.bank.api.controller;

import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.service.BalanceService;
import com.julio.bank.api.validation.EventValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BalanceService balanceService;

    @Test
    void shouldReturn200WithBalanceValue_whenAccountExists_thenBodyMatchesContract()
    {
        when(balanceService.getBalance("100")).thenReturn(new Balance("100", 20L));

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(get("/balance").param("account_id", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("20")));

    }

    @Test
    void shouldReturn404WithErrorBody_whenAccountDoesNotExist_thenMatchesContract()
    {
        when(balanceService.getBalance("1234")).thenThrow(new BalanceNotFoundException("1234"));

        Assertions.assertDoesNotThrow(() -> mockMvc.perform(get("/balance").param("account_id", "1234"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Account not found: 1234"))
                .andExpect(jsonPath("$.path").value("/balance")));
    }
}
