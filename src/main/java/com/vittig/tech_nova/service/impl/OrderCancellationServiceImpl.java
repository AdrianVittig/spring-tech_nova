package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.entity.OrderItem;
import com.vittig.tech_nova.data.util.OrderStatus;
import com.vittig.tech_nova.data.util.PaymentStatus;
import com.vittig.tech_nova.service.contract.InventoryService;
import com.vittig.tech_nova.service.contract.OrderCancellationService;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.contract.PaymentService;
import com.vittig.tech_nova.service.exception.ForbiddenOperationException;
import com.vittig.tech_nova.service.exception.InvalidStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderCancellationServiceImpl implements OrderCancellationService {
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final InventoryService inventoryService;
    @Override
    @Transactional
    public void cancelOrder(Long orderId, String email) {
        Order order = orderService.getOrderByIdEntity(orderId);
        if(!Objects.equals(order.getUser().getEmail(), email)){
            throw new ForbiddenOperationException("You do not have permission to cancel this order.");
        }
        if(order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus()  != OrderStatus.AWAITING_PAYMENT){
            throw new InvalidStatusException("Order cannot be cancelled in its current status.");
        }
        for(OrderItem item : order.getOrderItemList()){
            this.inventoryService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        if(order.getPayment() != null && order.getPayment().getPaymentStatus() == PaymentStatus.PENDING){
            this.paymentService.cancelPendingPayment(order.getId());
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
    }
}
