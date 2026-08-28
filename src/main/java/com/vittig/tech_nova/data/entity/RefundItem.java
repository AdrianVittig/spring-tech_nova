package com.vittig.tech_nova.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name = "refund_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefundItem extends BaseEntity{
    @ManyToOne
    @JoinColumn(nullable = false)
    private OrderItem item;
    private Integer quantity;
    private BigDecimal priceSnapshot;
    @ManyToOne
    private Refund refund;
}
