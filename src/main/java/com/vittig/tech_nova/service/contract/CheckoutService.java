package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.checkout.CheckoutDto;
import com.vittig.tech_nova.data.dto.payment.CreatePaymentDto;
import org.springframework.security.core.Authentication;

public interface CheckoutService {
    CheckoutDto processPayment(Long orderId, CreatePaymentDto createPaymentDto,  String email);
     void finalizeDuePayments();
}
