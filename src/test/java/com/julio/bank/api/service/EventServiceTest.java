package com.julio.bank.api.service;

import com.julio.bank.api.domain.Account;
import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.domain.EventType;
import com.julio.bank.api.entity.Event;
import com.julio.bank.api.exception.BalanceNotFoundException;
import com.julio.bank.api.repository.EventRepository;
import com.julio.bank.api.service.strategy.EventStrategy;
import com.julio.bank.api.service.strategy.EventStrategyResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventStrategyResolver strategyResolver;

    @Mock
    private EventStrategy strategy;

    @InjectMocks
    private EventService eventService;

    @Test
    void shouldSaveEvent_whenRepositoryPersistsIt_thenSavedEventIsReturned()
    {
        Event event = new Event("deposit", null, "100", 10L);
        when(eventRepository.save(event)).thenReturn(event);

        Event saved = eventService.save(event);

        assertThat(saved.getDestination()).isEqualTo("100");
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void shouldDeleteAllEvents_whenDeleteAllIsCalled_thenRepositoryDeleteAllIsInvoked()
    {
        eventService.deleteAll();
        verify(eventRepository, times(1)).deleteAll();
    }

    @Test
    void shouldPersistEventLog_whenStrategyExecutesSuccessfully_thenResultIsReturned()
    {
        EventRequest request = new EventRequest(EventType.DEPOSIT, null, "100", 10L);
        EventResult expectedResult = EventResult.of(null, new Account("100", 10L));

        when(strategyResolver.resolve(EventType.DEPOSIT)).thenReturn(strategy);
        when(strategy.execute(request)).thenReturn(expectedResult);

        EventResult result = eventService.process(request);

        assertThat(result).isEqualTo(expectedResult);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void shouldNotPersistEventLog_whenStrategyThrowsException_thenExceptionPropagates()
    {
        EventRequest request = new EventRequest(EventType.WITHDRAW, "999", null, 10L);

        when(strategyResolver.resolve(EventType.WITHDRAW)).thenReturn(strategy);
        when(strategy.execute(request)).thenThrow(new BalanceNotFoundException("999"));

        assertThatThrownBy(() -> eventService.process(request))
                .isInstanceOf(BalanceNotFoundException.class);

        verify(eventRepository, never()).save(any(Event.class));
    }
}
