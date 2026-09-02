package com.vittig.tech_nova.data.dto.product;

import com.vittig.tech_nova.data.util.Make;
import com.vittig.tech_nova.data.util.ProductType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductDto {
    @NotNull
    private Make make;
    @NotNull
    @Positive
    private BigDecimal priceToBuyFromReseller;
    @NotNull
    private ProductType productType;
}
