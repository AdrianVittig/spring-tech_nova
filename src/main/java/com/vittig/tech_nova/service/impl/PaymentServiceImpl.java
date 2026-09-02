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
import com.vittig.tech_nova.service.exception.InvalidStatusException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
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
                () -> new ObjectNotFoundException("Object not found!")
        ), PaymentDto.class);
    }

    @Override
    public Payment getPaymentByEntity(Long id) {
        return this.paymentRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Object not found!")
        );
    }

    @Override
    @Transactional
    public Payment createPaymentEntity(Long orderId, CreatePaymentDto createPaymentDto) {
        Order order = this.orderService.getOrderByIdEntity(orderId);
        Payment payment = new Payment();

        if(order.getOrderStatus() != OrderStatus.AWAITING_PAYMENT){
            throw new InvalidStatusException("The order is already paid!");
        }

        if(this.paymentRepository.existsByOrderId(order.getId())){
            throw new InvalidStatusException("Order already paid!");
        }

        if(createPaymentDto.getPaymentMethod() == null){
            throw new InvalidStatusException("Payment method is not chosen!");
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
            throw new InvalidStatusException("Payment status is not pending!");
        }

    }

    @Override
    @Transactional
    public void cancelPendingPayment(Long orderId) {
        Payment payment = this.paymentRepository.findPaymentByOrderIdForUpdate(orderId).orElseThrow(
                () -> new ObjectNotFoundException("Invalid input")
        );
        if(payment.getPaymentStatus() != PaymentStatus.PENDING){
            throw new InvalidStatusException("Status not pending");
        }
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
    }

    @Override
    @Transactional
    public PaymentDto getPaymentForOrder(Long orderId, String email) {
        Payment payment = this.paymentRepository.getPaymentByOrderId(orderId).orElseThrow(
                () -> new ObjectNotFoundException("Object not found!")
        );
        if(!Objects.equals(payment.getOrder().getUser().getEmail(), email)){
            throw new ObjectNotFoundException("User associated with this email does not own the order!");
        }
        return modelMapper.map(payment, PaymentDto.class);
    }
}
