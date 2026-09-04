package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.product.CreateProductDto;
import com.vittig.tech_nova.data.dto.product.ProductDto;
import com.vittig.tech_nova.data.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();

    ProductDto getProductById(Long id);

    ProductDto createProduct(CreateProductDto createProductDto);

    Product getProductEntityById(Long id);

    BigDecimal updateAverageCost(Long productId, BigDecimal newCost);
}
