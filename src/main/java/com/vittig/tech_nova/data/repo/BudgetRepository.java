package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Budget;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    @Query("SELECT b FROM Budget b")
    Optional<Budget> getBudget();

    @Query("SELECT b FROM Budget b")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Budget> getBudgetForUpdate();
}
