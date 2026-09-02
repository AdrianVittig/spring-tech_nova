package com.vittig.tech_nova.data.dto.payment;

import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.util.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentDto {
    private PaymentMethod paymentMethod;
}
