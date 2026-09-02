package com.vittig.tech_nova.web.controller;

import com.vittig.tech_nova.data.dto.checkout.CheckoutDto;
import com.vittig.tech_nova.data.dto.invoice.InvoiceDto;
import com.vittig.tech_nova.data.dto.order.CreateOrderDto;
import com.vittig.tech_nova.data.dto.order.OrderDto;
import com.vittig.tech_nova.data.dto.payment.CreatePaymentDto;
import com.vittig.tech_nova.data.dto.payment.PaymentDto;
import com.vittig.tech_nova.service.contract.CheckoutService;
import com.vittig.tech_nova.service.contract.InvoiceService;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.contract.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final CheckoutService checkoutService;

    @GetMapping
    public List<OrderDto> getCurrentUserOrders(Authentication authentication){
        return this.orderService.getOrdersByUserEmail(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@Valid @RequestBody CreateOrderDto createOrderDto, Authentication authentication){
        return this.orderService.createOrder(createOrderDto, authentication.getName());
    }

    @GetMapping("/{orderId}/invoice")
    public InvoiceDto getInvoiceForOrderById(@PathVariable Long orderId, Authentication authentication){
        return this.invoiceService.getInvoiceForOrder(orderId, authentication.getName());
    }

    @GetMapping("/{orderId}/payment")
    public PaymentDto getPaymentForOrder(@PathVariable Long orderId, Authentication authentication){
        return this.paymentService.getPaymentForOrder(orderId, authentication.getName());
    }

    @PostMapping("/{orderId}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutDto processPayment(@PathVariable Long orderId, @Valid @RequestBody CreatePaymentDto createPaymentDto, Authentication authentication){
        return this.checkoutService.processPayment(orderId, createPaymentDto, authentication.getName());
    }
}
