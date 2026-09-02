package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.budget.BudgetDto;
import com.vittig.tech_nova.data.entity.Budget;
import com.vittig.tech_nova.data.repo.BudgetRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.service.contract.BudgetService;
import com.vittig.tech_nova.service.exception.InsufficientBalanceException;
import com.vittig.tech_nova.service.exception.InvalidInputException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final ModelMapperUtil modelMapper;
    private final Long budgetId = 1L;
    @Override
    public BigDecimal getBalance() {
        Budget budget = this.budgetRepository.getBudget(budgetId).orElseThrow(
                () -> new ObjectNotFoundException("Budget not found.")
        );
        return budget.getBalance();
    }

    @Override
    @Transactional
    public BudgetDto increaseBalance(BigDecimal amount) {
        Budget budget = this.budgetRepository.getBudgetForUpdate(budgetId).orElseThrow(
                () -> new ObjectNotFoundException("Budget not found.")
        );
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidInputException("Amount must be greater than zero.");
        }
        budget.setBalance(budget.getBalance().add(amount));
        return modelMapper.map(this.budgetRepository.save(budget), BudgetDto.class);
    }

    @Override
    @Transactional
    public BudgetDto decreaseBalance(BigDecimal amount) {
        Budget budget = this.budgetRepository.getBudgetForUpdate(budgetId).orElseThrow(
                () -> new ObjectNotFoundException("Budget not found.")
        );
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidInputException("Amount must be greater than zero.");
        }
        if(budget.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0){
            throw new InsufficientBalanceException("Insufficient budget balance.");
        }
        budget.setBalance(budget.getBalance().subtract(amount));
        return modelMapper.map(this.budgetRepository.save(budget), BudgetDto.class);
    }
}
