package com.x.bank.bankXapp.mapper;

import com.x.bank.bankXapp.entity.Transaction;
import com.x.bank.bankXapp.record.PaymentRecord;
import com.x.bank.bankXapp.record.TransactionRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "transactionId", ignore = true)
    Transaction mapToEntity(PaymentRecord transactionRequest);


    default List<TransactionRecord> mapToRecords(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::mapToRecord)
                .toList();
    }

    TransactionRecord mapToRecord(Transaction transaction);
}
