package com.vittig.tech_nova.data.dto.payment;

import com.vittig.tech_nova.data.util.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentDto {
    @NotNull
    private PaymentMethod paymentMethod;
}
