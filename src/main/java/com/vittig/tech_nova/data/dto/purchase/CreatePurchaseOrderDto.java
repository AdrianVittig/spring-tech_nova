package com.vittig.tech_nova.data.dto.purchase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePurchaseOrderDto {
    private List<CreatePurchaseItemDto> items;
}
