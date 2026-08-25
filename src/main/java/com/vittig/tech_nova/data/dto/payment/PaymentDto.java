package com.vittig.tech_nova.data.dto.payment;

import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.util.PaymentMethod;
import com.vittig.tech_nova.data.util.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long id;
    private LocalDateTime time;
    private PaymentMethod paymentMethod;
    private Order order;
    private PaymentStatus paymentStatus;
}
