package com.vittig.tech_nova.data.dto.refund;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRefundDto {
    @NotNull
    @Positive
    private Long orderId;
    @NotEmpty
    @Valid
    private List<CreateRefundItemDto> items;
}
