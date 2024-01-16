package com.x.bank.bankXapp.repository;

import com.x.bank.bankXapp.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
