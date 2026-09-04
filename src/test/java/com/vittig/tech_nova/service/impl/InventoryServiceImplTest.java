package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.inventory.InventoryDto;
import com.vittig.tech_nova.data.entity.Inventory;
import com.vittig.tech_nova.data.repo.InventoryRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.service.exception.InvalidQuantityException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ModelMapperUtil modelMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;


    @Test
    void getInventoryByProductId_ShouldReturnInventory_WhenInventoryExists() {
        Long productId = 1L;

        Inventory inventory = new Inventory();
        InventoryDto expected = new InventoryDto();

        when(this.inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        when(this.modelMapper.map(inventory, InventoryDto.class))
                .thenReturn(expected);

        InventoryDto actual = this.inventoryService.getInventoryByProductId(productId);

        assertSame(expected, actual);

        verify(this.inventoryRepository).findByProductId(productId);
        verify(this.modelMapper).map(inventory, InventoryDto.class);
    }


    @Test
    void getInventoryByProductId_ShouldThrowException_WhenInventoryDoesNotExist() {
        Long productId = 1L;

        when(this.inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.inventoryService.getInventoryByProductId(productId)
        );

        verify(this.modelMapper, never())
                .map(any(Inventory.class), eq(InventoryDto.class));
    }


    @Test
    void decreaseStock_ShouldDecreaseStock_WhenQuantityIsValidAndStockIsEnough() {
        Long productId = 1L;
        Integer quantity = 3;

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(10);

        when(this.inventoryRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.of(inventory));

        Inventory actual = this.inventoryService.decreaseStock(productId, quantity);

        assertSame(inventory, actual);
        assertTrue(actual.getStockQuantity() == 7);

        verify(this.inventoryRepository).findByProductIdForUpdate(productId);
    }


    @Test
    void decreaseStock_ShouldAllowStockToBecomeZero() {
        Long productId = 1L;
        Integer quantity = 10;

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(10);

        when(this.inventoryRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.of(inventory));

        Inventory actual = this.inventoryService.decreaseStock(productId, quantity);

        assertTrue(actual.getStockQuantity() == 0);
    }


    @Test
    void decreaseStock_ShouldThrowException_WhenQuantityIsNull() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.decreaseStock(1L, null)
        );

        verify(this.inventoryRepository, never())
                .findByProductIdForUpdate(any());
    }


    @Test
    void decreaseStock_ShouldThrowException_WhenQuantityIsZero() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.decreaseStock(1L, 0)
        );

        verify(this.inventoryRepository, never())
                .findByProductIdForUpdate(any());
    }


    @Test
    void decreaseStock_ShouldThrowException_WhenQuantityIsNegative() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.decreaseStock(1L, -1)
        );

        verify(this.inventoryRepository, never())
                .findByProductIdForUpdate(any());
    }


    @Test
    void decreaseStock_ShouldThrowException_WhenInventoryDoesNotExist() {
        Long productId = 1L;

        when(this.inventoryRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.inventoryService.decreaseStock(productId, 2)
        );
    }


    @Test
    void decreaseStock_ShouldThrowException_WhenStockIsInsufficient() {
        Long productId = 1L;

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(5);

        when(this.inventoryRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.decreaseStock(productId, 10)
        );

        assertTrue(inventory.getStockQuantity() == 5);
    }


    @Test
    void increaseStock_ShouldIncreaseStock_WhenQuantityIsValid() {
        Long productId = 1L;

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(10);

        when(this.inventoryRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.of(inventory));

        Inventory actual = this.inventoryService.increaseStock(productId, 5);

        assertSame(inventory, actual);
        assertTrue(actual.getStockQuantity() == 15);

        verify(this.inventoryRepository).findByProductIdForUpdate(productId);
    }


    @Test
    void increaseStock_ShouldThrowException_WhenQuantityIsNull() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.increaseStock(1L, null)
        );

        verify(this.inventoryRepository, never())
                .findByProductIdForUpdate(any());
    }


    @Test
    void increaseStock_ShouldThrowException_WhenQuantityIsZero() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.increaseStock(1L, 0)
        );

        verify(this.inventoryRepository, never())
                .findByProductIdForUpdate(any());
    }


    @Test
    void increaseStock_ShouldThrowException_WhenQuantityIsNegative() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.increaseStock(1L, -3)
        );

        verify(this.inventoryRepository, never())
                .findByProductIdForUpdate(any());
    }


    @Test
    void increaseStock_ShouldThrowException_WhenInventoryDoesNotExist() {
        Long productId = 1L;

        when(this.inventoryRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.inventoryService.increaseStock(productId, 5)
        );
    }


    @Test
    void hasEnoughStock_ShouldReturnTrue_WhenStockIsEnough() {
        Long productId = 1L;

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(10);

        when(this.inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        boolean actual = this.inventoryService.hasEnoughStock(productId, 5);

        assertTrue(actual);
    }


    @Test
    void hasEnoughStock_ShouldReturnTrue_WhenRequiredQuantityEqualsStock() {
        Long productId = 1L;

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(10);

        when(this.inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        boolean actual = this.inventoryService.hasEnoughStock(productId, 10);

        assertTrue(actual);
    }


    @Test
    void hasEnoughStock_ShouldReturnFalse_WhenStockIsInsufficient() {
        Long productId = 1L;

        Inventory inventory = new Inventory();
        inventory.setStockQuantity(5);

        when(this.inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.of(inventory));

        boolean actual = this.inventoryService.hasEnoughStock(productId, 10);

        assertFalse(actual);
    }


    @Test
    void hasEnoughStock_ShouldThrowException_WhenRequiredQuantityIsNull() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.hasEnoughStock(1L, null)
        );

        verify(this.inventoryRepository, never())
                .findByProductId(any());
    }


    @Test
    void hasEnoughStock_ShouldThrowException_WhenRequiredQuantityIsZero() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.hasEnoughStock(1L, 0)
        );

        verify(this.inventoryRepository, never())
                .findByProductId(any());
    }


    @Test
    void hasEnoughStock_ShouldThrowException_WhenRequiredQuantityIsNegative() {
        assertThrows(
                InvalidQuantityException.class,
                () -> this.inventoryService.hasEnoughStock(1L, -1)
        );

        verify(this.inventoryRepository, never())
                .findByProductId(any());
    }


    @Test
    void hasEnoughStock_ShouldThrowException_WhenInventoryDoesNotExist() {
        Long productId = 1L;

        when(this.inventoryRepository.findByProductId(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.inventoryService.hasEnoughStock(productId, 5)
        );
    }


    @Test
    void getInventoryByProductIdForUpdate_ShouldReturnInventory_WhenInventoryExists() {
        Long productId = 1L;

        Inventory expected = new Inventory();

        when(this.inventoryRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.of(expected));

        Inventory actual =
                this.inventoryService.getInventoryByProductIdForUpdate(productId);

        assertSame(expected, actual);
    }


    @Test
    void getInventoryByProductIdForUpdate_ShouldThrowException_WhenInventoryDoesNotExist() {
        Long productId = 1L;

        when(this.inventoryRepository.findByProductIdForUpdate(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.inventoryService.getInventoryByProductIdForUpdate(productId)
        );
    }
}