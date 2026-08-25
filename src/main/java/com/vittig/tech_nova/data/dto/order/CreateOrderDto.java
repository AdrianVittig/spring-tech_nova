package com.vittig.tech_nova.data.dto.order;

import com.vittig.tech_nova.data.entity.OrderItem;
import com.vittig.tech_nova.data.entity.Payment;
import com.vittig.tech_nova.data.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDto {
    private List<OrderItemDto> items;
}
