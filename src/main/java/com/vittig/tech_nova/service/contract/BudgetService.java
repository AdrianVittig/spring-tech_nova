package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.budget.BudgetDto;

import java.math.BigDecimal;

public interface BudgetService {
    BigDecimal getBalance();

    BudgetDto increaseBalance(BigDecimal amount);

    BudgetDto decreaseBalance(BigDecimal amount);
}
