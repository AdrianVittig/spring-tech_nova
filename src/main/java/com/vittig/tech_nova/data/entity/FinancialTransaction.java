package com.vittig.tech_nova.data.entity;

import com.vittig.tech_nova.data.util.TransactionStatus;
import com.vittig.tech_nova.data.util.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialTransaction extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private LocalDateTime time;
    @PositiveOrZero
    private BigDecimal amount;
    @OneToOne
    @JoinColumn(unique = true)
    private Order order;
}
