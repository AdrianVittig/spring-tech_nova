package com.vittig.tech_nova.data.dto.order;

import com.vittig.tech_nova.data.entity.OrderItem;
import com.vittig.tech_nova.data.entity.Payment;
import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.util.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private List<OrderItem> orderItemList;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private Payment payment;
    private User user;
    private BigDecimal total;
}
