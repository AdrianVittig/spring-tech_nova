package com.vittig.tech_nova.web.controller;

import com.vittig.tech_nova.data.dto.purchase.CreatePurchaseOrderDto;
import com.vittig.tech_nova.data.dto.purchase.PurchaseOrderDto;
import com.vittig.tech_nova.service.contract.PurchaseOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/supply/orders")
public class SupplyController {
    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrderDto createPurchaseOrder(@Valid @RequestBody CreatePurchaseOrderDto dto){
        return this.purchaseOrderService.createPurchaseOrder(dto);
    }

    @PostMapping("/{id}/complete")
    public PurchaseOrderDto completePurchaseOrder(@PathVariable Long id){
        return this.purchaseOrderService.completePurchaseOrder(id);
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Long id){
        this.purchaseOrderService.cancelPurchaseOrder(id);
    }
}
