package com.x.bank.bankXapp.record;

import java.math.BigDecimal;

public record PaymentRecord(
        Long sendingAccountId,
        Long receivingAccountId,
        String senderReference,
        String receiverReference,
        BigDecimal amount) {
}
