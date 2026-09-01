package com.vittig.tech_nova.data.dto.transaction;

import com.vittig.tech_nova.data.util.TransactionStatus;
import com.vittig.tech_nova.data.util.TransactionType;
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
public class FTDto {
    private Long id;
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
    private LocalDateTime time;
    private BigDecimal amount;
    private Long orderId;
}
