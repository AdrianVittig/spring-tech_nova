package com.vittig.tech_nova.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseItem extends BaseEntity{
    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;
    @Positive
    private Integer quantity;
    @Positive
    private BigDecimal unitCostSnapshot;
    @ManyToOne
    @JoinColumn(nullable = false)
    private PurchaseOrder purchaseOrder;
}
