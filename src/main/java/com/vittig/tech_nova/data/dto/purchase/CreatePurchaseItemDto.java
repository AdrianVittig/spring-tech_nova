package com.vittig.tech_nova.data.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePurchaseItemDto {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitCost;
}
