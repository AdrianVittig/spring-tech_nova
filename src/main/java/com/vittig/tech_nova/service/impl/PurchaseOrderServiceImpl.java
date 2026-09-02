package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.purchase.CreatePurchaseItemDto;
import com.vittig.tech_nova.data.dto.purchase.CreatePurchaseOrderDto;
import com.vittig.tech_nova.data.dto.purchase.PurchaseOrderDto;
import com.vittig.tech_nova.data.entity.Inventory;
import com.vittig.tech_nova.data.entity.Product;
import com.vittig.tech_nova.data.entity.PurchaseItem;
import com.vittig.tech_nova.data.entity.PurchaseOrder;
import com.vittig.tech_nova.data.repo.PurchaseOrderRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.data.util.PurchaseOrderStatus;
import com.vittig.tech_nova.service.contract.*;
import com.vittig.tech_nova.service.exception.InvalidInputException;
import com.vittig.tech_nova.service.exception.InvalidQuantityException;
import com.vittig.tech_nova.service.exception.InvalidStatusException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductService productService;
    private final FinancialTransactionService financialTransactionService;
    private final ModelMapperUtil modelMapper;
    private final BudgetService budgetService;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public PurchaseOrderDto createPurchaseOrder(CreatePurchaseOrderDto createPurchaseOrderDto) {
        if(createPurchaseOrderDto == null){
            throw new InvalidInputException("Purchase order data is required.");
        }
        if(createPurchaseOrderDto.getItems() == null || createPurchaseOrderDto.getItems().isEmpty()){
            throw new InvalidInputException("Purchase order must contain at least one item.");
        }
        List<PurchaseItem> list = new ArrayList<>();
        PurchaseOrder order = new PurchaseOrder();
        order.setStatus(PurchaseOrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        for(CreatePurchaseItemDto item : createPurchaseOrderDto.getItems()){
            if(item.getQuantity() == null || item.getUnitCost() == null || item.getProductId() == null){
                throw new InvalidInputException("Product, quantity and unit cost are required for every purchase item.");
            }
            Product product = this.productService.getProductEntityById(item.getProductId());
            if(item.getQuantity() <= 0){
                throw new InvalidQuantityException("Purchase quantity must be greater than zero.");
            }
            if(item.getUnitCost().compareTo(BigDecimal.ZERO) <= 0){
                throw new InvalidInputException("Unit cost must be greater than zero.");
            }
            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setProduct(product);
            purchaseItem.setQuantity(item.getQuantity());
            purchaseItem.setUnitCostSnapshot(item.getUnitCost());
            purchaseItem.setPurchaseOrder(order);
            list.add(purchaseItem);
        }
        order.setItems(list);
        return modelMapper.map(this.purchaseOrderRepository.save(order), PurchaseOrderDto.class);
    }

    @Override
    @Transactional
    public PurchaseOrderDto completePurchaseOrder(Long purchaseId) {
        BigDecimal totalCost = BigDecimal.ZERO;
        PurchaseOrder order = this.purchaseOrderRepository.getPurchaseOrderByIdForUpdate(purchaseId)
                .orElseThrow(
                        () -> new ObjectNotFoundException("Purchase order not found.")
                );
        if(order.getStatus() != PurchaseOrderStatus.CREATED){
            throw new InvalidStatusException("Only a purchase order in CREATED status can be completed.");
        }
        for(PurchaseItem item : order.getItems()){
            BigDecimal currRow = item.getUnitCostSnapshot().multiply(BigDecimal.valueOf(item.getQuantity()));
            Inventory prodInventory = this.inventoryService.getInventoryByProductIdForUpdate(item.getProduct().getId());
            BigDecimal oldQty = BigDecimal.valueOf(prodInventory.getStockQuantity());
            BigDecimal oldCost = prodInventory.getProduct().getPriceToBuyFromReseller();
            BigDecimal incQuantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal incCost = item.getUnitCostSnapshot();
            BigDecimal oldStockValue = oldCost.multiply(oldQty);
            BigDecimal incStockValue = incCost.multiply(incQuantity);
            BigDecimal newAverageCost = (oldStockValue.add(incStockValue)).divide(oldQty.add(incQuantity), 2, RoundingMode.HALF_UP);
            this.productService.updateAverageCost(item.getProduct().getId(), newAverageCost);
            totalCost = totalCost.add(currRow);
            this.inventoryService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        this.budgetService.decreaseBalance(totalCost);
        this.financialTransactionService.recordPurchaseExpense(order, totalCost);
        order.setStatus(PurchaseOrderStatus.COMPLETED);
        return modelMapper.map(this.purchaseOrderRepository.save(order), PurchaseOrderDto.class);
    }

    @Override
    @Transactional
    public void cancelPurchaseOrder(Long purchaseId) {
        PurchaseOrder purchaseOrder = this.purchaseOrderRepository.getPurchaseOrderByIdForUpdate(purchaseId).orElseThrow(
                () -> new ObjectNotFoundException("Purchase order not found.")
        );
        if(purchaseOrder.getStatus() != PurchaseOrderStatus.CREATED){
            throw new InvalidStatusException("Only a purchase order in CREATED status can be cancelled.");
        }
        purchaseOrder.setStatus(PurchaseOrderStatus.CANCELLED);
        this.purchaseOrderRepository.save(purchaseOrder);
    }
}
