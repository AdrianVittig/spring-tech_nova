package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.order.CreateOrderDto;
import com.vittig.tech_nova.data.dto.order.OrderDto;
import com.vittig.tech_nova.data.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderDto> getAllOrders();
    List<OrderDto> getOrdersByUserEmail(String email);
    OrderDto getOrderById(Long id);
    OrderDto createOrder(CreateOrderDto createOrderDto, String email);
    Order getOrderByIdEntity(Long id);
    void markOrderAsPaid(Order order);
    Order getOrderByIdEntityForUpdate(Long id);
}
