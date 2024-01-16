package com.x.bank.bankXapp.record;

import com.x.bank.bankXapp.enums.TransactionStatus;

public record TransactionResultRecord(
        TransactionStatus status
) {}
