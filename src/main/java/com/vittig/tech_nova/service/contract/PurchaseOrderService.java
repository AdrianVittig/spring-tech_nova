package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.purchase.CreatePurchaseOrderDto;
import com.vittig.tech_nova.data.dto.purchase.PurchaseOrderDto;

public interface PurchaseOrderService {
    PurchaseOrderDto createPurchaseOrder(CreatePurchaseOrderDto createPurchaseOrderDto);
    PurchaseOrderDto completePurchaseOrder(Long purchaseId);
    void cancelPurchaseOrder(Long purchaseId);
}
