package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.product.CreateProductDto;
import com.vittig.tech_nova.data.dto.product.ProductDto;
import com.vittig.tech_nova.data.entity.Product;
import com.vittig.tech_nova.data.repo.ProductRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.service.contract.ProductService;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapperUtil modelMapper;

    @Override
    public List<ProductDto> getAllProducts() {
        return  modelMapper.mapList(this.productRepository.findAll(), ProductDto.class);
    }

    @Override
    public ProductDto getProductById(Long id) {
        return modelMapper.map(this.productRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Object not found!")
        ), ProductDto.class);
    }

    @Override
    public ProductDto createProduct(CreateProductDto createProductDto) {
        Product product = modelMapper.map(createProductDto, Product.class);
        return this.modelMapper.map(this.productRepository.save(product), ProductDto.class);
    }
}
