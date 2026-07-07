package com.julio.bank.api.repository;

import com.julio.bank.api.entity.Balance;
import com.julio.bank.api.repository.impl.BalanceRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BalanceRepositoryImpl.class)
class BalanceRepositoryTest {

    @Autowired
    private BalanceRepository balanceRepository;

    @Test
    void shouldPersistBalance_whenSaved_thenFindByIdReturnsIt()
    {
        balanceRepository.save(new Balance("100", 50L));

        Optional<Balance> found = balanceRepository.findById("100");

        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualTo(50L);
    }

    @Test
    void shouldReturnEmpty_whenBalanceIdDoesNotExist_thenNoResultIsFound()
    {
        assertThat(balanceRepository.findById("does-not-exist")).isEmpty();
    }

    @Test
    void shouldReturnBalance_whenFindByIdForUpdateIsCalledOnExistingBalanceId_thenBalanceIsReturned()
    {
        balanceRepository.save(new Balance("200", 15L));

        Optional<Balance> found = balanceRepository.findByIdForUpdate("200");

        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualTo(15L);
    }

    @Test
    void shouldReturnEmpty_whenFindByIdForUpdateIsCalledOnMissingBalanceId_thenNoResultIsFound() {
        assertThat(balanceRepository.findByIdForUpdate("does-not-exist")).isEmpty();
    }

    @Test
    void shouldUpdateBalance_whenSavedAgainWithNewValue_thenPersistedValueIsUpdated()
    {
        Balance balance = new Balance("300", 10L);
        balanceRepository.save(balance);

        balance.setBalance(30L);
        balanceRepository.save(balance);

        assertThat(balanceRepository.findById("300").get().getBalance()).isEqualTo(30L);
    }

    @Test
    void shouldRemoveAllBalances_whenDeleteAllIsCalled_thenNoneAreFound()
    {
        balanceRepository.save(new Balance("400", 10L));
        balanceRepository.save(new Balance("500", 20L));

        balanceRepository.deleteAll();

        assertThat(balanceRepository.findById("400")).isEmpty();
        assertThat(balanceRepository.findById("500")).isEmpty();
    }
}
