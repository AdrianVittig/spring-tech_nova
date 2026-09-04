package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.product.CreateProductDto;
import com.vittig.tech_nova.data.dto.product.ProductDto;
import com.vittig.tech_nova.data.entity.Product;
import com.vittig.tech_nova.data.repo.ProductRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ModelMapperUtil modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;


    @Test
    void getAllProducts_ShouldReturnMappedProducts() {
        Product product1 = new Product();
        Product product2 = new Product();

        List<Product> products = List.of(product1, product2);

        ProductDto productDto1 = mock(ProductDto.class);
        ProductDto productDto2 = mock(ProductDto.class);

        List<ProductDto> expected = List.of(productDto1, productDto2);

        when(this.productRepository.findAll())
                .thenReturn(products);

        when(this.modelMapper.mapList(products, ProductDto.class))
                .thenReturn(expected);

        List<ProductDto> actual = this.productService.getAllProducts();

        assertEquals(2, actual.size());
        assertSame(expected, actual);

        verify(this.productRepository).findAll();
        verify(this.modelMapper)
                .mapList(products, ProductDto.class);
    }


    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() {
        List<Product> products = List.of();
        List<ProductDto> expected = List.of();

        when(this.productRepository.findAll())
                .thenReturn(products);

        when(this.modelMapper.mapList(products, ProductDto.class))
                .thenReturn(expected);

        List<ProductDto> actual = this.productService.getAllProducts();

        assertEquals(0, actual.size());

        verify(this.productRepository).findAll();
    }


    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        Long productId = 1L;

        Product product = new Product();
        ProductDto expected = mock(ProductDto.class);

        when(this.productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(this.modelMapper.map(product, ProductDto.class))
                .thenReturn(expected);

        ProductDto actual = this.productService.getProductById(productId);

        assertSame(expected, actual);

        verify(this.productRepository).findById(productId);
        verify(this.modelMapper).map(product, ProductDto.class);
    }


    @Test
    void getProductById_ShouldThrowException_WhenProductDoesNotExist() {
        Long productId = 1L;

        when(this.productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.productService.getProductById(productId)
        );

        verify(this.modelMapper, never())
                .map(any(Product.class), eq(ProductDto.class));
    }


    @Test
    void createProduct_ShouldCreateAndReturnProduct_WhenInputIsValid() {
        CreateProductDto createProductDto = mock(CreateProductDto.class);

        Product mappedProduct = new Product();
        Product savedProduct = new Product();

        ProductDto expected = mock(ProductDto.class);

        when(this.modelMapper.map(createProductDto, Product.class))
                .thenReturn(mappedProduct);

        when(this.productRepository.save(mappedProduct))
                .thenReturn(savedProduct);

        when(this.modelMapper.map(savedProduct, ProductDto.class))
                .thenReturn(expected);

        ProductDto actual = this.productService.createProduct(createProductDto);

        assertSame(expected, actual);

        verify(this.modelMapper)
                .map(createProductDto, Product.class);

        verify(this.productRepository)
                .save(mappedProduct);

        verify(this.modelMapper)
                .map(savedProduct, ProductDto.class);
    }


    @Test
    void getProductEntityById_ShouldReturnEntity_WhenProductExists() {
        Long productId = 1L;

        Product expected = new Product();

        when(this.productRepository.findById(productId))
                .thenReturn(Optional.of(expected));

        Product actual = this.productService.getProductEntityById(productId);

        assertSame(expected, actual);

        verify(this.productRepository).findById(productId);
    }


    @Test
    void getProductEntityById_ShouldThrowException_WhenProductDoesNotExist() {
        Long productId = 1L;

        when(this.productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.productService.getProductEntityById(productId)
        );
    }


    @Test
    void updateAverageCost_ShouldUpdateProductCost_WhenProductExists() {
        Long productId = 1L;
        BigDecimal oldCost = new BigDecimal("100");
        BigDecimal newCost = new BigDecimal("125");

        Product product = new Product();
        product.setPriceToBuyFromReseller(oldCost);

        when(this.productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        BigDecimal actual =
                this.productService.updateAverageCost(productId, newCost);

        assertEquals(
                0,
                newCost.compareTo(actual)
        );

        assertEquals(
                0,
                newCost.compareTo(product.getPriceToBuyFromReseller())
        );

        verify(this.productRepository).findById(productId);
    }


    @Test
    void updateAverageCost_ShouldThrowException_WhenProductDoesNotExist() {
        Long productId = 1L;
        BigDecimal newCost = new BigDecimal("125");

        when(this.productRepository.findById(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.productService.updateAverageCost(
                        productId,
                        newCost
                )
        );
    }
}