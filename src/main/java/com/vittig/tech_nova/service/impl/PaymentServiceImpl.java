package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.payment.CreatePaymentDto;
import com.vittig.tech_nova.data.dto.payment.PaymentDto;
import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.entity.Payment;
import com.vittig.tech_nova.data.repo.PaymentRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.data.util.OrderStatus;
import com.vittig.tech_nova.data.util.PaymentMethod;
import com.vittig.tech_nova.data.util.PaymentStatus;
import com.vittig.tech_nova.service.contract.*;
import com.vittig.tech_nova.service.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final ModelMapperUtil modelMapper;
    private final OrderService orderService;
    @Override
    public List<PaymentDto> getAllPayments() {
        return modelMapper.mapList(this.paymentRepository.findAll(), PaymentDto.class);
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        return modelMapper.map(this.paymentRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Payment not found.")
        ), PaymentDto.class);
    }

    @Override
    public Payment getPaymentByEntity(Long id) {
        return this.paymentRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Payment not found.")
        );
    }

    @Override
    @Transactional
    public Payment createPaymentEntity(Long orderId, CreatePaymentDto createPaymentDto) {
        Order order = this.orderService.getOrderByIdEntity(orderId);
        Payment payment = new Payment();

        if(order.getOrderStatus() != OrderStatus.AWAITING_PAYMENT){
            throw new InvalidStatusException("Order is not awaiting payment.");
        }

        if(this.paymentRepository.existsByOrderId(order.getId())){
            throw new ConflictException("A payment already exists for this order.");
        }

        if(createPaymentDto.getPaymentMethod() == null){
            throw new InvalidInputException("Payment method is required.");
        }
        payment.setPaymentMethod(createPaymentDto.getPaymentMethod());

        if(createPaymentDto.getPaymentMethod().equals(PaymentMethod.CASH_ON_DELIVERY)){
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setTime(LocalDateTime.now().plusMinutes(5L));
        }else{
            payment.setTime(LocalDateTime.now());
            payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);

        }
        payment.setOrder(order);
        order.setPayment(payment);
        this.paymentRepository.save(payment);
        return payment;
    }

    @Override
    public List<Payment> getDuePaymentEntities() {
        return this.paymentRepository.findDuePayments(PaymentStatus.PENDING, LocalDateTime.now());
    }

    @Override
    public void markPaymentAsSuccessful(Payment payment){
        if(payment.getPaymentStatus() == PaymentStatus.PENDING){
            payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);
        }else{
            throw new InvalidStatusException("Only a pending payment can be marked as successful.");
        }

    }

    @Override
    @Transactional
    public void cancelPendingPayment(Long orderId) {
        Payment payment = this.paymentRepository.findPaymentByOrderIdForUpdate(orderId).orElseThrow(
                () -> new ObjectNotFoundException("Payment not found for this order.")
        );
        if(payment.getPaymentStatus() != PaymentStatus.PENDING){
            throw new InvalidStatusException("Only a pending payment can be cancelled.");
        }
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
    }

    @Override
    @Transactional
    public PaymentDto getPaymentForOrder(Long orderId, String email) {
        Payment payment = this.paymentRepository.getPaymentByOrderId(orderId).orElseThrow(
                () -> new ObjectNotFoundException("Payment not found for this order.")
        );
        if(!Objects.equals(payment.getOrder().getUser().getEmail(), email)){
            throw new ForbiddenOperationException("You do not have permission to access this payment.");
        }
        return modelMapper.map(payment, PaymentDto.class);
    }
}
