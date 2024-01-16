package com.x.bank.bankXapp.service;

import com.x.bank.bankXapp.dto.CustomerDto;
import com.x.bank.bankXapp.entity.Account;
import com.x.bank.bankXapp.entity.Customer;
import com.x.bank.bankXapp.enums.AccountType;
import com.x.bank.bankXapp.mapper.CustomerMapper;
import com.x.bank.bankXapp.repository.AccountRepository;
import com.x.bank.bankXapp.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    private final CustomerMapper customerMapper;

    public void onBoardCustomer(CustomerDto newCustomer) {
        Customer customer = customerMapper.mapToEntity(newCustomer);
        customer = customerRepository.save(customer);

        createAccountForCustomer(customer, AccountType.CURRENT);
        createAccountForCustomer(customer, AccountType.SAVINGS, BigDecimal.valueOf(500));
    }

    public Customer retrieveCustomerById(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow();
    }

    private Long createAccountForCustomer(Customer customer, AccountType accountType) {
        return createAccountForCustomer(customer, accountType, BigDecimal.ZERO);
    }

    private Long createAccountForCustomer(Customer customer, AccountType accountType, BigDecimal initialBalance) {
        Account newAccount = new Account();
        newAccount.setAccountType(accountType);
        newAccount.setAccountOwner(customer);
        newAccount.setBalance(initialBalance);

        return accountRepository.save(newAccount).getAccountId();
    }
}
