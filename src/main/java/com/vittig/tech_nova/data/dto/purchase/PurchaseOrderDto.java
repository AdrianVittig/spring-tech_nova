package com.vittig.tech_nova.data.dto.purchase;

import com.vittig.tech_nova.data.util.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderDto {
    private Long id;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private List<PurchaseItemDto> items;
}
