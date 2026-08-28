package com.vittig.tech_nova.data.dto.refund;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRefundDto {
    private Long orderId;
    private List<CreateRefundItemDto> items;
}
