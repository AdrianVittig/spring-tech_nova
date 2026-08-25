package com.vittig.tech_nova.data.dto.transaction;

import com.vittig.tech_nova.data.util.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFTDto {
    private TransactionStatus transactionStatus;
}
