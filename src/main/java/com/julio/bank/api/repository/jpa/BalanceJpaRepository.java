package com.julio.bank.api.repository.jpa;

import com.julio.bank.api.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BalanceJpaRepository extends JpaRepository<Balance, String> {

    @Modifying(clearAutomatically = true)
    @Query("update Balance b set b.balance = b.balance + :delta where b.id = :id")
    int addToBalance(@Param("id") String id, @Param("delta") Long delta);
}
