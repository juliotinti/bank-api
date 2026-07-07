package com.julio.bank.api.repository.jpa;

import com.julio.bank.api.entity.Balance;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BalanceJpaRepository extends JpaRepository<Balance, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("Select b from Balance b where b.id = :id")
    Optional<Balance> findByIdForUpdate(@Param("id") String id);
}
