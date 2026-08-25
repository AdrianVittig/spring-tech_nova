package com.vittig.tech_nova.data.dto.invoice;

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
public class InvoiceDto {
    private Long id;
    private Long orderId;
    private LocalDateTime issuedAt;
    private BigDecimal totalAmount;
    private Long invoiceNumber;
}
