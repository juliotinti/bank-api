package com.julio.bank.api.validation;

import com.julio.bank.api.domain.EventRequest;
import com.julio.bank.api.domain.EventType;
import com.julio.bank.api.exception.InvalidAmountException;
import com.julio.bank.api.exception.InvalidEventTypeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class EventValidationTest {

    @InjectMocks
    private EventValidation eventValidation;

    @Test
    void shouldReturnEventRequest_whenTypeAndAmountAreValid_thenFieldsAreMapped()
    {
        EventRequest request = eventValidation.validate("deposit", null, "100", 10L);

        assertThat(request.type()).isEqualTo(EventType.DEPOSIT);
        assertThat(request.destination()).isEqualTo("100");
        assertThat(request.amount()).isEqualTo(10L);
    }

    @Test
    void shouldParseTypeCaseInsensitively_whenRawTypeHasDifferentCasing_thenTypeMatches()
    {
        EventRequest request = eventValidation.validate("WiThDrAw", "100", null, 5L);

        assertThat(request.type()).isEqualTo(EventType.WITHDRAW);
    }

    @Test
    void shouldThrowInvalidAmountException_whenAmountIsNull_thenTypeIsNeverParsed()
    {
        assertThatThrownBy(() -> eventValidation.validate("deposit", null, "100", null))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount is required");
    }

    @Test
    void shouldThrowInvalidAmountException_whenAmountIsZero_thenValidationFails()
    {
        assertThatThrownBy(() -> eventValidation.validate("deposit", null, "100", 0L))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must be positive: 0");
    }

    @Test
    void shouldThrowInvalidAmountException_whenAmountIsNegative_thenValidationFails()
    {
        assertThatThrownBy(() -> eventValidation.validate("withdraw", "100", null, -10L))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must be positive: -10");
    }

    @Test
    void shouldThrowInvalidEventTypeException_whenRawTypeIsUnknown_thenValidationFails()
    {
        assertThatThrownBy(() -> eventValidation.validate("unknown", null, "100", 10L))
                .isInstanceOf(InvalidEventTypeException.class)
                .hasMessage("Unsupported event type: unknown");
    }

    @Test
    void shouldThrowInvalidEventTypeException_whenRawTypeIsNull_thenValidationFails()
    {
        assertThatThrownBy(() -> eventValidation.validate(null, null, "100", 10L))
                .isInstanceOf(InvalidEventTypeException.class)
                .hasMessage("Event type is required");
    }

    @Test
    void shouldThrowInvalidAmountException_whenBothAmountAndTypeAreInvalid_thenAmountIsCheckedFirst()
    {
        assertThatThrownBy(() -> eventValidation.validate("unknown", null, "100", -10L))
                .isInstanceOf(InvalidAmountException.class)
                .hasMessage("Amount must be positive: -10");
    }

    @Test
    void shouldThrowIllegalArgumentException_whenDepositDestinationIsMissing_thenValidationFails()
    {
        assertThatThrownBy(() -> eventValidation.validate("deposit", null, null, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Destination account is required");
    }

    @Test
    void shouldThrowIllegalArgumentException_whenWithdrawOriginIsMissing_thenValidationFails()
    {
        assertThatThrownBy(() -> eventValidation.validate("withdraw", null, null, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Origin account is required");
    }

    @Test
    void shouldThrowIllegalArgumentException_whenTransferFieldsAreMissing_thenValidationFails()
    {
        assertThatThrownBy(() -> eventValidation.validate("transfer", null, "300", 15L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Origin account is required");

        assertThatThrownBy(() -> eventValidation.validate("transfer", "100", " ", 15L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Destination account is required");
    }
}
