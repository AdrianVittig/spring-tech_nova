package com.vittig.tech_nova.config.init;

import com.vittig.tech_nova.data.entity.Budget;
import com.vittig.tech_nova.data.repo.BudgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BudgetInitializer implements CommandLineRunner {
    private final BudgetRepository budgetRepository;

    @Override
    public void run(String... args) {
        Optional<Budget> budget = this.budgetRepository.getBudget();
        if(budget.isEmpty()){
            Budget newBudget  = new Budget();
            newBudget .setBalance(BigDecimal.ZERO);
            this.budgetRepository.save(newBudget);
        }
    }
}
