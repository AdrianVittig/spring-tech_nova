package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.checkout.CheckoutDto;
import com.vittig.tech_nova.data.dto.payment.CreatePaymentDto;

public interface CheckoutService {
    CheckoutDto processPayment(CreatePaymentDto createPaymentDto);
     void finalizeDuePayments();
}
