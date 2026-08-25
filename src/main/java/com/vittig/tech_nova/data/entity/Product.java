package com.vittig.tech_nova.data.entity;

import com.vittig.tech_nova.data.util.Make;
import com.vittig.tech_nova.data.util.ProductType;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity{
    private Make make;
    @PositiveOrZero
    private BigDecimal priceToBuyFromReseller;
    @Enumerated(EnumType.STRING)
    private ProductType productType;
    @OneToOne
    private Inventory inventory;
}
