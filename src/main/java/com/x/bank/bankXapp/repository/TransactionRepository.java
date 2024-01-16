package com.x.bank.bankXapp.repository;

import com.x.bank.bankXapp.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllBySendingAccountId(Long sendingAccountId);

    List<Transaction> findAllByReceivingAccountId(Long receivingAccountId);

}
