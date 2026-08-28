package com.vittig.tech_nova.data.entity;

import com.vittig.tech_nova.data.util.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity{
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST)
    private List<OrderItem> orderItemList;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    @OneToOne(mappedBy = "order")
    private Payment payment;
    @ManyToOne
    private User user;
    @OneToOne(mappedBy = "order")
    private Invoice invoice;
    private BigDecimal total;
    @OneToMany(mappedBy = "order")
    private List<FinancialTransaction> financialTransactions;
}
