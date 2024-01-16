package com.x.bank.bankXapp.controller;


import com.x.bank.bankXapp.record.StandingOrderRecord;
import com.x.bank.bankXapp.service.CustomerService;
import com.x.bank.bankXapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("bankZ/")
public class bankZIntergrationController {

    private final TransactionService transactionService;
    private final CustomerService customerService;

    private final Long BANK_Z_ACCOUNT_ID = 2L;

    @PostMapping("debit/orders/process")
    public void processDebitOrders(@RequestBody List<StandingOrderRecord> debitOrders) {
        transactionService.processDebitOrders(debitOrders, customerService.retrieveCustomerById(BANK_Z_ACCOUNT_ID));
    }

    @PostMapping("credit/orders/process")
    public void processCreditOrders(@RequestBody List<StandingOrderRecord> creditOrders) {
        transactionService.processCreditOrders(creditOrders, customerService.retrieveCustomerById(BANK_Z_ACCOUNT_ID));
    }
}
