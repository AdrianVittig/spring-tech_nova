package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.service.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PricingServiceImplTest {
    private PricingServiceImpl pricingServiceImpl;

    @BeforeEach
    void setup(){
        pricingServiceImpl = new PricingServiceImpl();
    }

    @Test
    void calculateSellingPrice_ShouldReturnPriceWithMarkup_WhenPurchasePriceIsValid() {
        BigDecimal given = new BigDecimal("100");
        BigDecimal actual = this.pricingServiceImpl.calculateSellingPrice(given);
        BigDecimal expected = new BigDecimal("120.00");
        assertEquals(0, expected.compareTo(actual));
    }

    @Test
    void calculateSellingPrice_ShouldThrowException_WhenPurchasePriceIsNull() {
        assertThrows(InvalidInputException.class,
                () -> this.pricingServiceImpl.calculateSellingPrice(null));
    }

    @Test
    void calculateSellingPrice_ShouldThrowException_WhenPurchasePriceIsZero() {
        assertThrows(InvalidInputException.class,
                () -> this.pricingServiceImpl.calculateSellingPrice(BigDecimal.ZERO));
    }

    @Test
    void calculateSellingPrice_ShouldThrowException_WhenPurchasePriceIsNegative() {
        assertThrows(InvalidInputException.class,
                () -> this.pricingServiceImpl.calculateSellingPrice(new BigDecimal("-5")));
    }
}