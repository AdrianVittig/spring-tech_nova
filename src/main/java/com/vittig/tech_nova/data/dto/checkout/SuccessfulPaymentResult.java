package com.vittig.tech_nova.data.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SuccessfulPaymentResult {
    private Long invoiceId;
    private BigDecimal balanceAfterPayment;
}
