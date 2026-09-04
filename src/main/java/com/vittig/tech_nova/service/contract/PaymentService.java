package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.payment.CreatePaymentDto;
import com.vittig.tech_nova.data.dto.payment.PaymentDto;
import com.vittig.tech_nova.data.entity.Payment;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> getAllPayments();

    PaymentDto getPaymentById(Long id);

    Payment getPaymentByEntity(Long id);

    Payment createPaymentEntity(Long orderId, CreatePaymentDto createPaymentDto);

    List<Payment> getDuePaymentEntities();

    void markPaymentAsSuccessful(Payment payment);

    void cancelPendingPayment(Long orderId);

    PaymentDto getPaymentForOrder(Long orderId, String email);
}
