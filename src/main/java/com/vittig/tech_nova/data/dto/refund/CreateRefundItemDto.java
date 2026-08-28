package com.vittig.tech_nova.data.dto.refund;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRefundItemDto {
    private Long orderItemId;
    private Integer quantity;
}
