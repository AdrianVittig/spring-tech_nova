package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.refund.RefundDto;
import com.vittig.tech_nova.data.entity.Refund;
import com.vittig.tech_nova.data.entity.RefundItem;
import com.vittig.tech_nova.data.util.RefundStatus;
import com.vittig.tech_nova.service.contract.*;
import com.vittig.tech_nova.service.exception.InvalidStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RefundProcessingServiceImpl implements RefundProcessingService {
    private final RefundService refundService;
    private final BudgetService budgetService;
    private final FinancialTransactionService financialTransactionService;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public void processRefund(Long refundId) {
        Refund refund = this.refundService.getRefundByIdEntityForUpdate(refundId);
        if(refund.getRefundStatus() != RefundStatus.PENDING){
            throw new InvalidStatusException("Only a pending refund can be processed.");
        }
        this.budgetService.decreaseBalance(refund.getAmount());
        this.financialTransactionService.recordRefundOutcome(refund);
        for(RefundItem refundItem : refund.getRefundItemList()){
            this.inventoryService.increaseStock(refundItem.getItem().getProduct().getId(), refundItem.getQuantity());
        }
        this.refundService.markRefundAsSuccessful(refund);

    }
}
