package com.vittig.tech_nova.data.entity;

import com.vittig.tech_nova.data.util.RefundStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Refund extends BaseEntity{
    @ManyToOne
    @JoinColumn(nullable = false)
    private Order order;
    @Positive
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundStatus refundStatus;
    private LocalDateTime refundedAt;
    @OneToMany(mappedBy = "refund", cascade = CascadeType.PERSIST)
    private List<RefundItem> refundItemList;
    @OneToOne(mappedBy = "refund")
    private FinancialTransaction financialTransaction;
}
