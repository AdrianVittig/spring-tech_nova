package com.vittig.tech_nova.data.dto.purchase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty
    @Valid
    private List<CreatePurchaseItemDto> items;
}
