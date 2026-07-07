package com.julio.bank.api.repository;

import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.repository.impl.BalanceRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BalanceRepositoryImpl.class)
class BalanceRepositoryTest {

    @Autowired
    private BalanceRepository balanceRepository;

    @Test
    void shouldAddPositiveDeltaToExistingBalance() {
        balanceRepository.save(new Balance("200", 10L));

        int rowsAffected = balanceRepository.addToBalance("200", 10L);

        assertThat(rowsAffected).isEqualTo(1);
        assertThat(balanceRepository.findById("200")).isPresent();
        assertThat(balanceRepository.findById("200").get().getBalance()).isEqualTo(20L);
    }

    @Test
    void shouldSubtractWithNegativeDeltaFromExistingBalance() {
        balanceRepository.save(new Balance("300", 20L));

        int rowsAffected = balanceRepository.addToBalance("300", -5L);

        assertThat(rowsAffected).isEqualTo(1);
        assertThat(balanceRepository.findById("300").get().getBalance()).isEqualTo(15L);
    }

    @Test
    void shouldReturnZeroRowsAffectedWhenAddingToBalanceThatDoesNotExist() {
        int rowsAffected = balanceRepository.addToBalance("does-not-exist", 10L);

        assertThat(rowsAffected).isZero();
    }

}
