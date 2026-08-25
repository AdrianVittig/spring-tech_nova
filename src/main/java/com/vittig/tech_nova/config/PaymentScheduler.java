package com.vittig.tech_nova.config;

import com.vittig.tech_nova.service.contract.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentScheduler {
    private final CheckoutService checkoutService;
    @Scheduled(fixedDelay = 30000L)
    public void checkDuePayments(){
        this.checkoutService.finalizeDuePayments();
    }
}
