package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.service.contract.PricingService;
import com.vittig.tech_nova.service.exception.InvalidInputException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingServiceImpl implements PricingService {
    private static final BigDecimal MARKUP_MULTIPLIER = new BigDecimal("1.20");
    @Override
    public BigDecimal calculateSellingPrice(BigDecimal purchasePrice) {
        if(purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidInputException("Purchase price must be greater than zero.");
        }
        return purchasePrice.multiply(MARKUP_MULTIPLIER);
    }
}
