package com.x.bank.bankXapp.record;

import java.math.BigDecimal;

public record StandingOrderRecord(
        Long accountId,
        BigDecimal amount
) {
}
