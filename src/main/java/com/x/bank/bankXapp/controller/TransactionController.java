package com.x.bank.bankXapp.controller;

import com.x.bank.bankXapp.record.PaymentRecord;
import com.x.bank.bankXapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("transaction/")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("pay")
    public ResponseEntity<?> makePaymentBetweenAccounts(@RequestBody PaymentRecord transactionRequest) {
        return ResponseEntity.ok(transactionService.makePaymentBetweenAccounts(transactionRequest));
    }

    @GetMapping("{accountId}/history")
    public ResponseEntity<?> retrieveAllTransactionsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.retrieveAllTransactionsByAccountId(accountId));
    }
}
