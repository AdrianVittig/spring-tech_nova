package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.FinancialTransaction;
import com.vittig.tech_nova.data.util.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    boolean existsByOrderIdAndTransactionType(Long orderId, TransactionType type);
    boolean existsByRefundId(Long refundId);
    boolean existsByPurchaseOrderId(Long purchaseOrderId);
}
