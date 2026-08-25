package com.vittig.tech_nova.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Inventory extends BaseEntity {
    @OneToOne
    @JoinColumn(unique = true, nullable = false)
    private Product product;
    @PositiveOrZero
    private Integer stockQuantity = 0;
}
