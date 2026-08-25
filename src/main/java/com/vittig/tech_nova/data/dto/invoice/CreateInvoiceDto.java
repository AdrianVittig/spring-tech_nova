package com.vittig.tech_nova.data.dto.invoice;

import com.vittig.tech_nova.data.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateInvoiceDto {
    private Order order;
}
