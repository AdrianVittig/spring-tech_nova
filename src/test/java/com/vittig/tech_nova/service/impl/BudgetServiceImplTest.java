package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.budget.BudgetDto;
import com.vittig.tech_nova.data.entity.Budget;
import com.vittig.tech_nova.data.repo.BudgetRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.service.exception.InsufficientBalanceException;
import com.vittig.tech_nova.service.exception.InvalidInputException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {
    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private ModelMapperUtil modelMapper;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    @Test
    void getBalance_ShouldReturnCurrentBalance_WhenBudgetExists() {
        Budget budget = new Budget();
        budget.setBalance(new BigDecimal("100"));

        when(this.budgetRepository.getBudget()).thenReturn(Optional.of(budget));

        BigDecimal actual = this.budgetService.getBalance();

        assertEquals(0, new BigDecimal("100").compareTo(actual));
    }

    @Test
    void getBalance_ShouldThrowException_WhenBudgetDoesNotExist() {
        assertThrows(ObjectNotFoundException.class, () -> this.budgetService.getBalance());
    }

    @Test
    void increaseBalance_ShouldIncreaseBalance_WhenAmountIsValid() {
        Budget budget = new Budget();
        budget.setBalance(new BigDecimal("100"));

        BudgetDto budgetDto = new BudgetDto();
        budgetDto.setBalance(new BigDecimal("115"));

        when(this.budgetRepository.getBudgetForUpdate())
                .thenReturn(Optional.of(budget));

        when(this.budgetRepository.save(any(Budget.class)))
                .thenReturn(budget);

        when(this.modelMapper.map(any(Budget.class), eq(BudgetDto.class)))
                .thenReturn(budgetDto);

        BudgetDto actual = this.budgetService.increaseBalance(new BigDecimal("15"));

        assertEquals(0, actual.getBalance().compareTo(new BigDecimal("115")));
    }

    @Test
    void increaseBalance_ShouldThrowException_WhenAmountIsNull() {
        assertThrows(
                InvalidInputException.class,
                () -> this.budgetService.increaseBalance(null)
        );

        verify(this.budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void increaseBalance_ShouldThrowException_WhenAmountIsZero() {
        assertThrows(
                InvalidInputException.class,
                () -> this.budgetService.increaseBalance(BigDecimal.ZERO)
        );

        verify(this.budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void increaseBalance_ShouldThrowException_WhenAmountIsNegative() {
        assertThrows(
                InvalidInputException.class,
                () -> this.budgetService.increaseBalance(new BigDecimal("-5"))
        );

        verify(this.budgetRepository, never()).save(any(Budget.class));
    }

    @Test
    void decreaseBalance_ShouldDecreaseBalance_WhenAmountIsValid() {
        Budget budget = new Budget();
        budget.setBalance(new BigDecimal("100"));

        BudgetDto budgetDto = new BudgetDto();
        budgetDto.setBalance(new BigDecimal("75"));

        when(this.budgetRepository.getBudgetForUpdate())
                .thenReturn(Optional.of(budget));

        when(this.budgetRepository.save(any(Budget.class)))
                .thenReturn(budget);

        when(this.modelMapper.map(any(Budget.class), eq(BudgetDto.class)))
                .thenReturn(budgetDto);

        BudgetDto actual = this.budgetService.decreaseBalance(
                new BigDecimal("25")
        );

        assertEquals(
                0,
                new BigDecimal("75").compareTo(actual.getBalance())
        );

        assertEquals(
                0,
                new BigDecimal("75").compareTo(budget.getBalance())
        );

        verify(this.budgetRepository).save(budget);
    }


    @Test
    void decreaseBalance_ShouldAllowBalanceToBecomeZero() {
        Budget budget = new Budget();
        budget.setBalance(new BigDecimal("100"));

        BudgetDto budgetDto = new BudgetDto();
        budgetDto.setBalance(BigDecimal.ZERO);

        when(this.budgetRepository.getBudgetForUpdate())
                .thenReturn(Optional.of(budget));

        when(this.budgetRepository.save(any(Budget.class)))
                .thenReturn(budget);

        when(this.modelMapper.map(any(Budget.class), eq(BudgetDto.class)))
                .thenReturn(budgetDto);

        BudgetDto actual = this.budgetService.decreaseBalance(
                new BigDecimal("100")
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(actual.getBalance())
        );

        assertEquals(
                0,
                BigDecimal.ZERO.compareTo(budget.getBalance())
        );
    }


    @Test
    void decreaseBalance_ShouldThrowException_WhenAmountIsNull() {
        assertThrows(
                InvalidInputException.class,
                () -> this.budgetService.decreaseBalance(null)
        );

        verify(this.budgetRepository, never())
                .getBudgetForUpdate();

        verify(this.budgetRepository, never())
                .save(any(Budget.class));
    }


    @Test
    void decreaseBalance_ShouldThrowException_WhenAmountIsZero() {
        assertThrows(
                InvalidInputException.class,
                () -> this.budgetService.decreaseBalance(BigDecimal.ZERO)
        );

        verify(this.budgetRepository, never())
                .getBudgetForUpdate();

        verify(this.budgetRepository, never())
                .save(any(Budget.class));
    }


    @Test
    void decreaseBalance_ShouldThrowException_WhenAmountIsNegative() {
        assertThrows(
                InvalidInputException.class,
                () -> this.budgetService.decreaseBalance(
                        new BigDecimal("-10")
                )
        );

        verify(this.budgetRepository, never())
                .getBudgetForUpdate();

        verify(this.budgetRepository, never())
                .save(any(Budget.class));
    }


    @Test
    void decreaseBalance_ShouldThrowException_WhenBudgetDoesNotExist() {
        when(this.budgetRepository.getBudgetForUpdate())
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.budgetService.decreaseBalance(
                        new BigDecimal("10")
                )
        );

        verify(this.budgetRepository, never())
                .save(any(Budget.class));
    }


    @Test
    void decreaseBalance_ShouldThrowException_WhenBalanceIsInsufficient() {
        Budget budget = new Budget();
        budget.setBalance(new BigDecimal("50"));

        when(this.budgetRepository.getBudgetForUpdate())
                .thenReturn(Optional.of(budget));

        assertThrows(
                InsufficientBalanceException.class,
                () -> this.budgetService.decreaseBalance(
                        new BigDecimal("100")
                )
        );

        assertEquals(
                0,
                new BigDecimal("50").compareTo(budget.getBalance())
        );

        verify(this.budgetRepository, never())
                .save(any(Budget.class));
    }
}