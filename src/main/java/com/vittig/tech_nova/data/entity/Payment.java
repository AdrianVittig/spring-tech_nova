package com.vittig.tech_nova.data.entity;

import com.vittig.tech_nova.data.util.PaymentMethod;
import com.vittig.tech_nova.data.util.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends BaseEntity {
    private LocalDateTime time;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @OneToOne
    private Order order;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
}
