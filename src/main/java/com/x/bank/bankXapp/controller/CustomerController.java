package com.x.bank.bankXapp.controller;

import com.x.bank.bankXapp.dto.CustomerDto;
import com.x.bank.bankXapp.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("customer/")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("create")
    public void createCustomerAccount(@RequestBody CustomerDto newCustomer) {
        customerService.onBoardCustomer(newCustomer);
    }
}
