package com.vittig.tech_nova.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Invoice extends BaseEntity{
    @OneToOne
    @JoinColumn(unique = true)
    private Order order;
    private LocalDateTime issuedAt;
    private BigDecimal totalAmount;
    @Column(unique = true)
    private Long invoiceNumber;
}
