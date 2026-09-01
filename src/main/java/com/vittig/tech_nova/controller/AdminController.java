package com.vittig.tech_nova.controller;

import com.vittig.tech_nova.data.dto.order.OrderDto;
import com.vittig.tech_nova.data.dto.product.CreateProductDto;
import com.vittig.tech_nova.data.dto.product.ProductDto;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.contract.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ProductService productService;
    private final OrderService orderService;
    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@Valid @RequestBody CreateProductDto createProductDto){
        return this.productService.createProduct(createProductDto);
    }

    @GetMapping("/orders")
    public List<OrderDto> getAllOrders(){
        return this.orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public OrderDto getOrderById(@PathVariable Long id){
        return this.orderService.getOrderById(id);
    }
}
