package com.x.bank.bankXapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private Long sendingAccountId;

    private Long receivingAccountId;

    private String senderReference;

    private String receiverReference;

    private LocalDateTime transactionDateTime;

    private BigDecimal amount;
}
