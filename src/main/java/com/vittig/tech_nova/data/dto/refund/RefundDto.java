package com.vittig.tech_nova.data.dto.refund;

import com.vittig.tech_nova.data.util.RefundStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefundDto {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private RefundStatus refundStatus;
    private LocalDateTime refundedAt;
}
