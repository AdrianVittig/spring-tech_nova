package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.entity.Inventory;
import com.vittig.tech_nova.data.repo.InventoryRepository;
import com.vittig.tech_nova.service.contract.InventoryService;
import com.vittig.tech_nova.service.exception.InvalidQuantityException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    public Inventory getInventoryByProductId(Long productId) {
        return this.inventoryRepository.findByProductId(productId).orElseThrow(
                () -> new ObjectNotFoundException("Inventory not found!")
        );
    }

    @Override
    @Transactional
    public Inventory decreaseStock(Long productId, Integer quantity) {
        if(quantity == null || quantity <= 0){
            throw new InvalidQuantityException("Quantity must be a positive number");
        }
        Inventory inventory = this.inventoryRepository.findByProductIdForUpdate(productId).orElseThrow(
                () -> new ObjectNotFoundException("Inventory not found!")
        );
        if(inventory.getStockQuantity() < quantity){
            throw new InvalidQuantityException("Insufficient quantity!");
        }
        inventory.setStockQuantity(inventory.getStockQuantity() - quantity);
        return inventory;
    }

    @Override
    @Transactional
    public Inventory increaseStock(Long productId, Integer quantity) {
        if(quantity == null || quantity <= 0){
            throw new InvalidQuantityException("Quantity must be a positive number");
        }
        Inventory inventory = this.inventoryRepository.findByProductIdForUpdate(productId).orElseThrow(
                () -> new ObjectNotFoundException("Inventory not found!")
        );
        inventory.setStockQuantity(inventory.getStockQuantity()+quantity);
        return inventory;
    }

    @Override
    public boolean hasEnoughStock(Long productId, Integer requiredQuantity) {
        if(requiredQuantity == null || requiredQuantity <= 0){
            throw new InvalidQuantityException("Quantity must be a positive number");
        }
        Inventory inventory = this.inventoryRepository.findByProductId(productId).orElseThrow(
                () -> new ObjectNotFoundException("Inventory not found!")
        );
        return inventory.getStockQuantity() >= requiredQuantity;
    }
}
