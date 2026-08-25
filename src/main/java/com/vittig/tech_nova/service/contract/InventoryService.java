package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.entity.Inventory;

public interface InventoryService {
    Inventory getInventoryByProductId(Long productId);
    Inventory decreaseStock(Long productId, Integer quantity);
    Inventory increaseStock(Long productId, Integer quantity);
    boolean hasEnoughStock(Long productId, Integer requiredQuantity);
}
