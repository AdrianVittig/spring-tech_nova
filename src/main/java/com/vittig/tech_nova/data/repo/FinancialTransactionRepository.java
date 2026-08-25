package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinancialTransactionRepository extends JpaRepository<FinancialTransaction, Long> {
    boolean existsByOrderId(Long orderId);
}
