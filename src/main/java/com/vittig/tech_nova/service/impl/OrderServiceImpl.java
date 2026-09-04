package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.order.CreateOrderDto;
import com.vittig.tech_nova.data.dto.order.OrderDto;
import com.vittig.tech_nova.data.dto.order.OrderItemDto;
import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.entity.OrderItem;
import com.vittig.tech_nova.data.entity.Product;
import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.repo.OrderRepository;
import com.vittig.tech_nova.data.repo.ProductRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.data.util.OrderStatus;
import com.vittig.tech_nova.service.contract.InventoryService;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.contract.PricingService;
import com.vittig.tech_nova.service.contract.UserService;
import com.vittig.tech_nova.service.exception.InvalidInputException;
import com.vittig.tech_nova.service.exception.InvalidQuantityException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ModelMapperUtil modelMapper;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final UserService userService;
    private final PricingService pricingService;

    @Override
    public List<OrderDto> getAllOrders() {
        return modelMapper.mapList(this.orderRepository.findAll(), OrderDto.class);
    }

    @Override
    public List<OrderDto> getOrdersByUserEmail(String email) {
        return modelMapper.mapList(this.orderRepository.getAllOrdersByUserEmail(email), OrderDto.class);
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return modelMapper.map(this.orderRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Order not found.")
        ), OrderDto.class);
    }

    @Override
    @Transactional
    public OrderDto createOrder(CreateOrderDto createOrderDto, String email) {
        BigDecimal total = BigDecimal.ZERO;
        Order order = new Order();
        order.setOrderItemList(new ArrayList<>());
        order.setOrderStatus(OrderStatus.AWAITING_PAYMENT);
        order.setCreatedAt(LocalDateTime.now());
        User user = this.userService.getUserEntityByEmail(email);
        order.setUser(user);
        if (createOrderDto.getItems() == null || createOrderDto.getItems().isEmpty()) {
            throw new InvalidInputException("Order must contain at least one item.");
        }
        for (OrderItemDto item : createOrderDto.getItems()) {
            Product product = this.productRepository.findById(item.getProductId()).orElseThrow(
                    () -> new ObjectNotFoundException("Product not found.")
            );
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new InvalidQuantityException("Order item quantity must be greater than zero.");
            }
            BigDecimal priceToSell = this.pricingService.calculateSellingPrice(product.getPriceToBuyFromReseller());
            total = total.add(priceToSell.multiply(BigDecimal.valueOf(item.getQuantity())));
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPriceSnapshot(priceToSell);
            orderItem.setOrder(order);
            order.getOrderItemList().add(orderItem);
            inventoryService.decreaseStock(product.getId(), item.getQuantity());
        }
        order.setTotal(total);
        this.orderRepository.save(order);
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public Order getOrderByIdEntity(Long id) {
        return this.orderRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Order not found.")
        );
    }

    @Override
    @Transactional
    public void markOrderAsPaid(Order order) {
        if (order.getOrderStatus() == OrderStatus.AWAITING_PAYMENT) {
            order.setOrderStatus(OrderStatus.PAID);
        }
    }

    @Override
    @Transactional
    public Order getOrderByIdEntityForUpdate(Long id) {
        return this.orderRepository.findByIdForUpdate(id).orElseThrow(
                () -> new ObjectNotFoundException("Order not found.")
        );
    }


}
