package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.transaction.FTDto;
import com.vittig.tech_nova.data.dto.transaction.UpdateFTDto;
import com.vittig.tech_nova.data.entity.PurchaseOrder;
import com.vittig.tech_nova.data.entity.Refund;

import java.math.BigDecimal;
import java.util.List;

public interface FinancialTransactionService {
    List<FTDto> getAllTransactions();

    FTDto getTransactionById(Long id);

    FTDto finalizeTransaction(Long id, UpdateFTDto ftDto);

    FTDto recordPaymentIncome(Long orderId);

    FTDto recordRefundOutcome(Refund refund);

    FTDto recordPurchaseExpense(PurchaseOrder purchaseOrder, BigDecimal totalCost);
}
