package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.product.CreateProductDto;
import com.vittig.tech_nova.data.dto.product.ProductDto;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Long id);
    ProductDto createProduct(CreateProductDto createProductDto);
}
