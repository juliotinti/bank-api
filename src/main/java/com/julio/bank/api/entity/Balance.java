package com.julio.bank.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "balance", nullable = false)
    private Long balance;

    public void deposit(Long amount) {
        this.balance += amount;
    }

    public void withdraw(Long amount) {
        this.balance -= amount;
    }
}
