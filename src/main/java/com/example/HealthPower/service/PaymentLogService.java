package com.example.HealthPower.service;

import com.example.HealthPower.entity.board.Product;
import com.example.HealthPower.entity.payment.TransactionHistory;
import com.example.HealthPower.entity.payment.TransactionType;
import com.example.HealthPower.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentLogService {

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String userId, Long amount, double currentBalance, Product product, int quantity) {
        transactionHistoryRepository.save(new TransactionHistory(
                userId,
                TransactionType.FAILURE,
                amount,
                currentBalance,
                product.getProductName(),
                quantity
        ));
    }
}
