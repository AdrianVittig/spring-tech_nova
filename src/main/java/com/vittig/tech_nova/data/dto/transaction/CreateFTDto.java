package com.vittig.tech_nova.data.dto.transaction;

import com.vittig.tech_nova.data.util.TransactionStatus;
import com.vittig.tech_nova.data.util.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFTDto {
    private TransactionStatus transactionStatus;
    private TransactionType transactionType;
    private BigDecimal amount;
    private Long orderId;
}
