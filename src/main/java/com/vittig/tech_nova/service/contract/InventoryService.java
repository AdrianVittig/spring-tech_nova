package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.inventory.InventoryDto;
import com.vittig.tech_nova.data.entity.Inventory;

public interface InventoryService {
    InventoryDto getInventoryByProductId(Long productId);
    Inventory decreaseStock(Long productId, Integer quantity);
    Inventory increaseStock(Long productId, Integer quantity);
    boolean hasEnoughStock(Long productId, Integer requiredQuantity);
    Inventory getInventoryByProductIdForUpdate(Long productId);
}
