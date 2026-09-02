package com.vittig.tech_nova.service.contract;

public interface OrderCancellationService {
    void cancelOrder(Long orderId, String email);
}
