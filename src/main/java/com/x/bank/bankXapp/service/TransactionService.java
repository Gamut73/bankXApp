package com.x.bank.bankXapp.service;

import com.x.bank.bankXapp.entity.Account;
import com.x.bank.bankXapp.entity.Customer;
import com.x.bank.bankXapp.entity.Transaction;
import com.x.bank.bankXapp.enums.AccountType;
import com.x.bank.bankXapp.enums.TransactionStatus;
import com.x.bank.bankXapp.exception.TransactionException;
import com.x.bank.bankXapp.mapper.TransactionMapper;
import com.x.bank.bankXapp.record.PaymentRecord;
import com.x.bank.bankXapp.record.StandingOrderRecord;
import com.x.bank.bankXapp.record.TransactionRecord;
import com.x.bank.bankXapp.record.TransactionResultRecord;
import com.x.bank.bankXapp.repository.AccountRepository;
import com.x.bank.bankXapp.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;

    private final Long ADMIN_ACCOUNT_ID = 1L;
    private final BigDecimal TRANSACTION_ADMIN_FEE = BigDecimal.valueOf(0.0005);
    private final BigDecimal SAVINGS_ACCOUNT_DEPOSIT_BONUS = BigDecimal.valueOf(0.005);

    public void processCreditOrders(List<StandingOrderRecord> creditOrders, Customer bank) {
        creditOrders.stream().map(creditOrder -> {
            PaymentRecord creditOrderPayment = new PaymentRecord(
                    bank.getCustomerId(),
                    creditOrder.accountId(),
                    String.format("credit-order-account-%d", creditOrder.accountId()),
                    String.format("credit-order-%s", bank.getFirstName()),
                    creditOrder.amount()
            );

            return creditOrderPayment;
        }).forEach(this::makePaymentBetweenAccounts);
    }

    public void processDebitOrders(List<StandingOrderRecord> debitOrders, Customer bank) {
        debitOrders.stream().map(debitOrder -> {
            PaymentRecord debitOrderPayment = new PaymentRecord(
                    debitOrder.accountId(),
                    bank.getCustomerId(),
                    String.format("debit-order-%s", bank.getFirstName()),
                    String.format("debit-order-account-%d", debitOrder.accountId()),
                    debitOrder.amount()
            );

            return debitOrderPayment;
        }).forEach(this::makePaymentBetweenAccounts);
    }

    public List<TransactionRecord> retrieveAllTransactionsByAccountId(Long accountId) {

        List<TransactionRecord> incomingTransactions = transactionMapper.mapToRecords(transactionRepository.findAllByReceivingAccountId(accountId));
        List<TransactionRecord> accountTransactionHistory = new ArrayList<>(incomingTransactions);
        List<TransactionRecord> outgoingTransactions = transactionMapper.mapToRecords(transactionRepository.findAllBySendingAccountId(accountId));
        accountTransactionHistory.addAll(outgoingTransactions);

        return accountTransactionHistory.stream()
                .sorted(Comparator.comparing(TransactionRecord::transactionDateTime))
                .collect(Collectors.toList());
    }

    public TransactionResultRecord makePaymentBetweenAccounts(PaymentRecord transactionRequest) {

        Account sendingAccount = findAccountById(transactionRequest.sendingAccountId());
        Account receivingAccount = findAccountById(transactionRequest.receivingAccountId());

        if (sendingAccount.getAccountType() != AccountType.CURRENT) {
            throw new TransactionException("Only a current account can send money");
        }

        if (sendingAccount.getBalance().compareTo(transactionRequest.amount()) < 0) {
            return new TransactionResultRecord(TransactionStatus.INSUFFICIENT_FUNDS);
        }

        BigDecimal totalCostToSender = calculateToTalCostToSender(transactionRequest.amount());

        updateSenderAccount(sendingAccount, totalCostToSender);
        updateReceiverAccount(receivingAccount, transactionRequest.amount());
        savePaymentTransaction(transactionRequest, totalCostToSender);

        notifyCustomersOfTransaction(
                sendingAccount.getAccountOwner().getEmail(),
                sendingAccount.getAccountOwner().getFirstName(),
                receivingAccount.getAccountOwner().getEmail(),
                receivingAccount.getAccountOwner().getFirstName(),
                transactionRequest.amount());

        return new TransactionResultRecord(TransactionStatus.SUCCESSFUL);
    }

    private void notifyCustomersOfTransaction(String senderEmail, String senderName, String receiverEmail, String receiverName, BigDecimal amount) {
        notificationService.sendEmail(
                senderEmail,
                "Payment Successful",
                String.format("Your R%f payment to %s was successful", amount.doubleValue(), receiverName));

        notificationService.sendEmail(
                receiverEmail,
                "Payment Received",
                String.format("A R%f payment from %s has been made to your account", amount.doubleValue(), senderName));
    }

    private void savePaymentTransaction(PaymentRecord transactionRequest, BigDecimal totalCostToSender) {
        Transaction transaction = transactionMapper.mapToEntity(transactionRequest);
        transaction.setTransactionDateTime(LocalDateTime.now());
        transactionRepository.save(transaction);

        Transaction adminTransaction = new Transaction();
        adminTransaction.setSendingAccountId(transaction.getSendingAccountId());
        adminTransaction.setReceivingAccountId(ADMIN_ACCOUNT_ID);
        adminTransaction.setSenderReference("admin fee");
        adminTransaction.setReceiverReference(String.format("transaction-%d-admin-fee", transaction.getTransactionId()));
        adminTransaction.setTransactionDateTime(LocalDateTime.now());
        adminTransaction.setAmount(totalCostToSender.subtract(transactionRequest.amount()));

        transactionRepository.save(adminTransaction);
    }

    private void updateReceiverAccount(Account receivingAccount, BigDecimal amount) {
        BigDecimal amountToAdd = amount;

        if (receivingAccount.getAccountType() == AccountType.SAVINGS) {
            amountToAdd = amount.multiply(SAVINGS_ACCOUNT_DEPOSIT_BONUS).add(amount);
            saveSavingsAccountBonusTransaction(receivingAccount.getAccountId(), amount.multiply(SAVINGS_ACCOUNT_DEPOSIT_BONUS));
        }

        receivingAccount.setBalance(receivingAccount.getBalance().add(amountToAdd));

        accountRepository.save(receivingAccount);
    }

    private void saveSavingsAccountBonusTransaction(Long receiverAccountId, BigDecimal bonusAmount) {
        Transaction transaction = new Transaction();
        transaction.setSendingAccountId(ADMIN_ACCOUNT_ID);
        transaction.setReceivingAccountId(receiverAccountId);
        transaction.setSenderReference(String.format("savings-account-%d-deposit-bonus", receiverAccountId));
        transaction.setReceiverReference("savings-account-deposit-bonus");
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setAmount(bonusAmount);

        transactionRepository.save(transaction);
    }

    private void updateSenderAccount(Account sendingAccount, BigDecimal amount) {
        sendingAccount.setBalance(sendingAccount.getBalance().subtract(amount));

        accountRepository.save(sendingAccount);
    }

    private BigDecimal calculateToTalCostToSender(BigDecimal amount) {
        return amount.multiply(TRANSACTION_ADMIN_FEE).add(amount);
    }

    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new TransactionException(String.format("Account with Id: %d not found", accountId)));
    }

}
