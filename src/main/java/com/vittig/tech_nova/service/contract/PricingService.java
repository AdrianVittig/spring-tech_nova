package com.vittig.tech_nova.service.contract;

import java.math.BigDecimal;

public interface PricingService {
    BigDecimal calculateSellingPrice(BigDecimal purchasePrice);
}
