package com.x.bank.bankXapp.entity;

import com.x.bank.bankXapp.enums.AccountType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    private BigDecimal balance;

    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer accountOwner;
}
