package com.vittig.tech_nova.data.dto.product;

import com.vittig.tech_nova.data.entity.Inventory;
import com.vittig.tech_nova.data.util.Make;
import com.vittig.tech_nova.data.util.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private Make make;
    private BigDecimal priceToBuyFromReseller;
    private ProductType productType;
    private Inventory inventory;
}
