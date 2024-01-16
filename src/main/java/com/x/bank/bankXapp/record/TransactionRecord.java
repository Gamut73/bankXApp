package com.x.bank.bankXapp.record;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRecord(Long sendingAccountId,
                                Long receivingAccountId,
                                String senderReference,
                                String receiverReference,
                                LocalDateTime transactionDateTime,
                                BigDecimal amount) {
}
