package com.x.bank.bankXapp.repository;

import com.x.bank.bankXapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
