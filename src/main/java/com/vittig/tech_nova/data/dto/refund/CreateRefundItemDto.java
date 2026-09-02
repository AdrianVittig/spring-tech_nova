package com.vittig.tech_nova.data.dto.refund;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRefundItemDto {
    @NotNull
    @Positive
    private Long orderItemId;
    @NotNull
    @Positive
    private Integer quantity;
}
