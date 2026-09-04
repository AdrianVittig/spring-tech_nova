package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.payment.CreatePaymentDto;
import com.vittig.tech_nova.data.dto.payment.PaymentDto;
import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.entity.Payment;
import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.repo.PaymentRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.data.util.OrderStatus;
import com.vittig.tech_nova.data.util.PaymentMethod;
import com.vittig.tech_nova.data.util.PaymentStatus;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.exception.ConflictException;
import com.vittig.tech_nova.service.exception.ForbiddenOperationException;
import com.vittig.tech_nova.service.exception.InvalidInputException;
import com.vittig.tech_nova.service.exception.InvalidStatusException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ModelMapperUtil modelMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private PaymentServiceImpl paymentService;


    @Test
    void getAllPayments_ShouldReturnMappedPayments() {
        Payment payment1 = new Payment();
        Payment payment2 = new Payment();

        List<Payment> payments = List.of(payment1, payment2);

        PaymentDto dto1 = new PaymentDto();
        PaymentDto dto2 = new PaymentDto();

        List<PaymentDto> expected = List.of(dto1, dto2);

        when(this.paymentRepository.findAll())
                .thenReturn(payments);

        when(this.modelMapper.mapList(payments, PaymentDto.class))
                .thenReturn(expected);

        List<PaymentDto> actual = this.paymentService.getAllPayments();

        assertSame(expected, actual);
        assertEquals(2, actual.size());

        verify(this.paymentRepository).findAll();
        verify(this.modelMapper).mapList(payments, PaymentDto.class);
    }


    @Test
    void getAllPayments_ShouldReturnEmptyList_WhenNoPaymentsExist() {
        List<Payment> payments = List.of();
        List<PaymentDto> expected = List.of();

        when(this.paymentRepository.findAll())
                .thenReturn(payments);

        when(this.modelMapper.mapList(payments, PaymentDto.class))
                .thenReturn(expected);

        List<PaymentDto> actual = this.paymentService.getAllPayments();

        assertTrue(actual.isEmpty());
    }


    @Test
    void getPaymentById_ShouldReturnPayment_WhenPaymentExists() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        PaymentDto expected = new PaymentDto();

        when(this.paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        when(this.modelMapper.map(payment, PaymentDto.class))
                .thenReturn(expected);

        PaymentDto actual = this.paymentService.getPaymentById(paymentId);

        assertSame(expected, actual);

        verify(this.paymentRepository).findById(paymentId);
        verify(this.modelMapper).map(payment, PaymentDto.class);
    }


    @Test
    void getPaymentById_ShouldThrowException_WhenPaymentDoesNotExist() {
        Long paymentId = 1L;

        when(this.paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.paymentService.getPaymentById(paymentId)
        );

        verify(this.modelMapper, never())
                .map(any(Payment.class), eq(PaymentDto.class));
    }


    @Test
    void getPaymentByEntity_ShouldReturnEntity_WhenPaymentExists() {
        Long paymentId = 1L;

        Payment expected = new Payment();

        when(this.paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(expected));

        Payment actual = this.paymentService.getPaymentByEntity(paymentId);

        assertSame(expected, actual);
    }


    @Test
    void getPaymentByEntity_ShouldThrowException_WhenPaymentDoesNotExist() {
        Long paymentId = 1L;

        when(this.paymentRepository.findById(paymentId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.paymentService.getPaymentByEntity(paymentId)
        );
    }


    @Test
    void createPaymentEntity_ShouldCreateSuccessfulPayment_WhenMethodIsCard() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.AWAITING_PAYMENT);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentMethod(PaymentMethod.CARD);

        when(this.orderService.getOrderByIdEntity(orderId))
                .thenReturn(order);

        when(this.paymentRepository.existsByOrderId(orderId))
                .thenReturn(false);

        LocalDateTime before = LocalDateTime.now();

        Payment actual =
                this.paymentService.createPaymentEntity(orderId, dto);

        LocalDateTime after = LocalDateTime.now();

        assertEquals(PaymentMethod.CARD, actual.getPaymentMethod());
        assertEquals(PaymentStatus.SUCCESSFUL, actual.getPaymentStatus());

        assertSame(order, actual.getOrder());
        assertSame(actual, order.getPayment());

        assertNotNull(actual.getTime());

        assertFalse(actual.getTime().isBefore(before));
        assertFalse(actual.getTime().isAfter(after));

        verify(this.paymentRepository).save(actual);
    }


    @Test
    void createPaymentEntity_ShouldCreatePendingPayment_WhenMethodIsCashOnDelivery() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.AWAITING_PAYMENT);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentMethod(PaymentMethod.CASH_ON_DELIVERY);

        when(this.orderService.getOrderByIdEntity(orderId))
                .thenReturn(order);

        when(this.paymentRepository.existsByOrderId(orderId))
                .thenReturn(false);

        LocalDateTime before =
                LocalDateTime.now().plusMinutes(5);

        Payment actual =
                this.paymentService.createPaymentEntity(orderId, dto);

        LocalDateTime after =
                LocalDateTime.now().plusMinutes(5);

        assertEquals(
                PaymentMethod.CASH_ON_DELIVERY,
                actual.getPaymentMethod()
        );

        assertEquals(
                PaymentStatus.PENDING,
                actual.getPaymentStatus()
        );

        assertSame(order, actual.getOrder());
        assertSame(actual, order.getPayment());

        assertNotNull(actual.getTime());

        assertFalse(actual.getTime().isBefore(before));
        assertFalse(actual.getTime().isAfter(after));

        verify(this.paymentRepository).save(actual);
    }


    @Test
    void createPaymentEntity_ShouldThrowException_WhenOrderIsNotAwaitingPayment() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.PAID);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentMethod(PaymentMethod.CARD);

        when(this.orderService.getOrderByIdEntity(orderId))
                .thenReturn(order);

        assertThrows(
                InvalidStatusException.class,
                () -> this.paymentService.createPaymentEntity(
                        orderId,
                        dto
                )
        );

        verify(this.paymentRepository, never())
                .existsByOrderId(any());

        verify(this.paymentRepository, never())
                .save(any(Payment.class));
    }


    @Test
    void createPaymentEntity_ShouldThrowException_WhenPaymentAlreadyExistsForOrder() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.AWAITING_PAYMENT);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentMethod(PaymentMethod.CARD);

        when(this.orderService.getOrderByIdEntity(orderId))
                .thenReturn(order);

        when(this.paymentRepository.existsByOrderId(orderId))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> this.paymentService.createPaymentEntity(
                        orderId,
                        dto
                )
        );

        verify(this.paymentRepository, never())
                .save(any(Payment.class));
    }


    @Test
    void createPaymentEntity_ShouldThrowException_WhenPaymentMethodIsNull() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.AWAITING_PAYMENT);

        CreatePaymentDto dto = new CreatePaymentDto();
        dto.setPaymentMethod(null);

        when(this.orderService.getOrderByIdEntity(orderId))
                .thenReturn(order);

        when(this.paymentRepository.existsByOrderId(orderId))
                .thenReturn(false);

        assertThrows(
                InvalidInputException.class,
                () -> this.paymentService.createPaymentEntity(
                        orderId,
                        dto
                )
        );

        verify(this.paymentRepository, never())
                .save(any(Payment.class));
    }


    @Test
    void getDuePaymentEntities_ShouldReturnDuePendingPayments() {
        Payment payment1 = new Payment();
        payment1.setPaymentStatus(PaymentStatus.PENDING);

        Payment payment2 = new Payment();
        payment2.setPaymentStatus(PaymentStatus.PENDING);

        List<Payment> expected = List.of(payment1, payment2);

        when(this.paymentRepository.findDuePayments(
                eq(PaymentStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(expected);

        List<Payment> actual =
                this.paymentService.getDuePaymentEntities();

        assertSame(expected, actual);
        assertEquals(2, actual.size());

        verify(this.paymentRepository).findDuePayments(
                eq(PaymentStatus.PENDING),
                any(LocalDateTime.class)
        );
    }


    @Test
    void markPaymentAsSuccessful_ShouldChangeStatus_WhenPaymentIsPending() {
        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);

        this.paymentService.markPaymentAsSuccessful(payment);

        assertEquals(
                PaymentStatus.SUCCESSFUL,
                payment.getPaymentStatus()
        );
    }


    @Test
    void markPaymentAsSuccessful_ShouldThrowException_WhenPaymentIsNotPending() {
        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);

        assertThrows(
                InvalidStatusException.class,
                () -> this.paymentService.markPaymentAsSuccessful(payment)
        );

        assertEquals(
                PaymentStatus.SUCCESSFUL,
                payment.getPaymentStatus()
        );
    }


    @Test
    void cancelPendingPayment_ShouldCancelPayment_WhenPaymentIsPending() {
        Long orderId = 1L;

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);

        when(this.paymentRepository.findPaymentByOrderIdForUpdate(orderId))
                .thenReturn(Optional.of(payment));

        this.paymentService.cancelPendingPayment(orderId);

        assertEquals(
                PaymentStatus.CANCELLED,
                payment.getPaymentStatus()
        );

        verify(this.paymentRepository)
                .findPaymentByOrderIdForUpdate(orderId);
    }


    @Test
    void cancelPendingPayment_ShouldThrowException_WhenPaymentDoesNotExist() {
        Long orderId = 1L;

        when(this.paymentRepository.findPaymentByOrderIdForUpdate(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.paymentService.cancelPendingPayment(orderId)
        );
    }


    @Test
    void cancelPendingPayment_ShouldThrowException_WhenPaymentIsNotPending() {
        Long orderId = 1L;

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.SUCCESSFUL);

        when(this.paymentRepository.findPaymentByOrderIdForUpdate(orderId))
                .thenReturn(Optional.of(payment));

        assertThrows(
                InvalidStatusException.class,
                () -> this.paymentService.cancelPendingPayment(orderId)
        );

        assertEquals(
                PaymentStatus.SUCCESSFUL,
                payment.getPaymentStatus()
        );
    }


    @Test
    void getPaymentForOrder_ShouldReturnMappedPayment_WhenUserOwnsOrder() {
        Long orderId = 1L;
        String email = "customer@test.com";

        User user = new User();
        user.setEmail(email);

        Order order = new Order();
        order.setUser(user);

        Payment payment = new Payment();
        payment.setOrder(order);

        PaymentDto expected = new PaymentDto();

        when(this.paymentRepository.getPaymentByOrderId(orderId))
                .thenReturn(Optional.of(payment));

        when(this.modelMapper.map(payment, PaymentDto.class))
                .thenReturn(expected);

        PaymentDto actual =
                this.paymentService.getPaymentForOrder(orderId, email);

        assertSame(expected, actual);

        verify(this.modelMapper)
                .map(payment, PaymentDto.class);
    }


    @Test
    void getPaymentForOrder_ShouldThrowException_WhenPaymentDoesNotExist() {
        Long orderId = 1L;

        when(this.paymentRepository.getPaymentByOrderId(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.paymentService.getPaymentForOrder(
                        orderId,
                        "customer@test.com"
                )
        );

        verify(this.modelMapper, never())
                .map(any(Payment.class), eq(PaymentDto.class));
    }


    @Test
    void getPaymentForOrder_ShouldThrowException_WhenUserDoesNotOwnOrder() {
        Long orderId = 1L;

        User owner = new User();
        owner.setEmail("owner@test.com");

        Order order = new Order();
        order.setUser(owner);

        Payment payment = new Payment();
        payment.setOrder(order);

        when(this.paymentRepository.getPaymentByOrderId(orderId))
                .thenReturn(Optional.of(payment));

        assertThrows(
                ForbiddenOperationException.class,
                () -> this.paymentService.getPaymentForOrder(
                        orderId,
                        "other@test.com"
                )
        );

        verify(this.modelMapper, never())
                .map(any(Payment.class), eq(PaymentDto.class));
    }
}