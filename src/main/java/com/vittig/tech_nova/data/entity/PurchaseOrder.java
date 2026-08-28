package com.vittig.tech_nova.data.entity;

import com.vittig.tech_nova.data.util.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrder extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.PERSIST)
    private List<PurchaseItem> items;
    @OneToOne(mappedBy = "purchaseOrder")
    private FinancialTransaction financialTransaction;
}
