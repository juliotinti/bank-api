package com.julio.bank.api.controller;

import com.julio.bank.api.service.ResetService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResetController.class)
class ResetControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResetService resetService;

    @Test
    void shouldReturn200_whenResetIsCalled_thenResetServiceIsInvoked()
    {
        Assertions.assertDoesNotThrow(() -> mockMvc.perform(post("/reset"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK")));

        verify(resetService, times(1)).resetAll();
    }
}
