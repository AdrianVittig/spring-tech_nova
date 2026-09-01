package com.vittig.tech_nova.controller;

import com.vittig.tech_nova.data.dto.product.ProductDto;
import com.vittig.tech_nova.service.contract.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts(){
        return this.productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id){
        return this.productService.getProductById(id);
    }
}
