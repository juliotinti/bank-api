package com.julio.bank.api.service.strategy;

import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventResult;
import com.julio.bank.api.domain.EventType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventStrategyResolverTest {

    private final EventStrategy depositStrategy = mockedStrategy(EventType.DEPOSIT);
    private final EventStrategy withdrawStrategy = mockedStrategy(EventType.WITHDRAW);

    private final EventStrategyResolver resolver =
            new EventStrategyResolver(List.of(depositStrategy, withdrawStrategy));

    @Test
    void shouldResolveStrategy_whenTypeIsRegistered_thenMatchingStrategyIsReturned()
    {
        assertThat(resolver.resolve(EventType.DEPOSIT)).isSameAs(depositStrategy);
        assertThat(resolver.resolve(EventType.WITHDRAW)).isSameAs(withdrawStrategy);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenTypeIsNotRegistered_thenResolveFails()
    {
        assertThatThrownBy(() -> resolver.resolve(EventType.TRANSFER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TRANSFER");
    }

    private EventStrategy mockedStrategy(EventType type)
    {
        return new EventStrategy()
        {
            @Override
            public EventType supportedType()
            {
                return type;
            }

            @Override
            public EventResult execute(EventRequest request)
            {
                return null;
            }
        };
    }
}
