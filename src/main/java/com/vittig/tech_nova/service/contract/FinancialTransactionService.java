package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.transaction.CreateFTDto;
import com.vittig.tech_nova.data.dto.transaction.FTDto;
import com.vittig.tech_nova.data.dto.transaction.UpdateFTDto;
import com.vittig.tech_nova.data.entity.FinancialTransaction;

import java.util.List;
import java.util.Optional;

public interface FinancialTransactionService {
    List<FTDto> getAllTransactions();
    FTDto getTransactionById(Long id);
    FTDto finalizeTransaction(Long id, UpdateFTDto ftDto);
    FTDto recordPaymentIncome(Long orderId);
}
