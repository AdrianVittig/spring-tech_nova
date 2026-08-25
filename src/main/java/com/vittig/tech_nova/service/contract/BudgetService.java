package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.budget.BudgetDto;
import com.vittig.tech_nova.data.entity.Budget;

import java.math.BigDecimal;

public interface BudgetService {
    BigDecimal getBalance();
    BudgetDto increaseBalance(BigDecimal amount);
}
