package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.order.CreateOrderDto;
import com.vittig.tech_nova.data.dto.order.OrderDto;
import com.vittig.tech_nova.data.dto.order.OrderItemDto;
import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.entity.OrderItem;
import com.vittig.tech_nova.data.entity.Product;
import com.vittig.tech_nova.data.entity.User;
import com.vittig.tech_nova.data.repo.OrderRepository;
import com.vittig.tech_nova.data.repo.ProductRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.data.util.OrderStatus;
import com.vittig.tech_nova.service.contract.InventoryService;
import com.vittig.tech_nova.service.contract.PricingService;
import com.vittig.tech_nova.service.contract.UserService;
import com.vittig.tech_nova.service.exception.InvalidInputException;
import com.vittig.tech_nova.service.exception.InvalidQuantityException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ModelMapperUtil modelMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private UserService userService;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private OrderServiceImpl orderService;


    @Test
    void getAllOrders_ShouldReturnMappedOrders() {
        Order order1 = new Order();
        Order order2 = new Order();

        List<Order> orders = List.of(order1, order2);

        OrderDto orderDto1 = mock(OrderDto.class);
        OrderDto orderDto2 = mock(OrderDto.class);

        List<OrderDto> expected = List.of(orderDto1, orderDto2);

        when(this.orderRepository.findAll())
                .thenReturn(orders);

        when(this.modelMapper.mapList(orders, OrderDto.class))
                .thenReturn(expected);

        List<OrderDto> actual = this.orderService.getAllOrders();

        assertSame(expected, actual);
        assertEquals(2, actual.size());

        verify(this.orderRepository).findAll();
        verify(this.modelMapper).mapList(orders, OrderDto.class);
    }


    @Test
    void getAllOrders_ShouldReturnEmptyList_WhenNoOrdersExist() {
        List<Order> orders = List.of();
        List<OrderDto> expected = List.of();

        when(this.orderRepository.findAll())
                .thenReturn(orders);

        when(this.modelMapper.mapList(orders, OrderDto.class))
                .thenReturn(expected);

        List<OrderDto> actual = this.orderService.getAllOrders();

        assertTrue(actual.isEmpty());
    }


    @Test
    void getOrdersByUserEmail_ShouldReturnMappedOrders() {
        String email = "customer@test.com";

        Order order1 = new Order();
        Order order2 = new Order();

        List<Order> orders = List.of(order1, order2);

        OrderDto orderDto1 = mock(OrderDto.class);
        OrderDto orderDto2 = mock(OrderDto.class);

        List<OrderDto> expected = List.of(orderDto1, orderDto2);

        when(this.orderRepository.getAllOrdersByUserEmail(email))
                .thenReturn(orders);

        when(this.modelMapper.mapList(orders, OrderDto.class))
                .thenReturn(expected);

        List<OrderDto> actual =
                this.orderService.getOrdersByUserEmail(email);

        assertSame(expected, actual);
        assertEquals(2, actual.size());

        verify(this.orderRepository)
                .getAllOrdersByUserEmail(email);
    }


    @Test
    void getOrderById_ShouldReturnMappedOrder_WhenOrderExists() {
        Long orderId = 1L;

        Order order = new Order();
        OrderDto expected = mock(OrderDto.class);

        when(this.orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(this.modelMapper.map(order, OrderDto.class))
                .thenReturn(expected);

        OrderDto actual = this.orderService.getOrderById(orderId);

        assertSame(expected, actual);

        verify(this.orderRepository).findById(orderId);
        verify(this.modelMapper).map(order, OrderDto.class);
    }


    @Test
    void getOrderById_ShouldThrowException_WhenOrderDoesNotExist() {
        Long orderId = 1L;

        when(this.orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.orderService.getOrderById(orderId)
        );

        verify(this.modelMapper, never())
                .map(any(Order.class), eq(OrderDto.class));
    }


    @Test
    void createOrder_ShouldCreateOrder_WhenInputIsValid() {
        String email = "customer@test.com";

        User user = new User();

        CreateOrderDto createOrderDto = mock(CreateOrderDto.class);
        OrderItemDto itemDto = mock(OrderItemDto.class);

        Product product = new Product();
        product.setId(10L);
        product.setPriceToBuyFromReseller(new BigDecimal("100"));

        OrderDto expected = mock(OrderDto.class);

        when(createOrderDto.getItems())
                .thenReturn(List.of(itemDto));

        when(itemDto.getProductId())
                .thenReturn(10L);

        when(itemDto.getQuantity())
                .thenReturn(2);

        when(this.userService.getUserEntityByEmail(email))
                .thenReturn(user);

        when(this.productRepository.findById(10L))
                .thenReturn(Optional.of(product));

        when(this.pricingService.calculateSellingPrice(
                new BigDecimal("100")
        )).thenReturn(new BigDecimal("120"));

        when(this.modelMapper.map(any(Order.class), eq(OrderDto.class)))
                .thenReturn(expected);

        OrderDto actual =
                this.orderService.createOrder(createOrderDto, email);

        assertSame(expected, actual);

        ArgumentCaptor<Order> orderCaptor =
                ArgumentCaptor.forClass(Order.class);

        verify(this.orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertSame(user, savedOrder.getUser());
        assertEquals(OrderStatus.AWAITING_PAYMENT, savedOrder.getOrderStatus());
        assertNotNull(savedOrder.getCreatedAt());

        assertEquals(
                0,
                new BigDecimal("240").compareTo(savedOrder.getTotal())
        );

        assertEquals(1, savedOrder.getOrderItemList().size());

        OrderItem savedItem = savedOrder.getOrderItemList().getFirst();

        assertSame(product, savedItem.getProduct());
        assertEquals(2, savedItem.getQuantity());

        assertEquals(
                0,
                new BigDecimal("120")
                        .compareTo(savedItem.getUnitPriceSnapshot())
        );

        assertSame(savedOrder, savedItem.getOrder());

        verify(this.inventoryService)
                .decreaseStock(10L, 2);

        verify(this.pricingService)
                .calculateSellingPrice(new BigDecimal("100"));
    }


    @Test
    void createOrder_ShouldCalculateCorrectTotal_WhenOrderContainsMultipleItems() {
        String email = "customer@test.com";

        User user = new User();

        CreateOrderDto createOrderDto = mock(CreateOrderDto.class);

        OrderItemDto item1 = mock(OrderItemDto.class);
        OrderItemDto item2 = mock(OrderItemDto.class);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setPriceToBuyFromReseller(new BigDecimal("100"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setPriceToBuyFromReseller(new BigDecimal("50"));

        when(createOrderDto.getItems())
                .thenReturn(List.of(item1, item2));

        when(item1.getProductId()).thenReturn(1L);
        when(item1.getQuantity()).thenReturn(2);

        when(item2.getProductId()).thenReturn(2L);
        when(item2.getQuantity()).thenReturn(3);

        when(this.userService.getUserEntityByEmail(email))
                .thenReturn(user);

        when(this.productRepository.findById(1L))
                .thenReturn(Optional.of(product1));

        when(this.productRepository.findById(2L))
                .thenReturn(Optional.of(product2));

        when(this.pricingService.calculateSellingPrice(
                new BigDecimal("100")
        )).thenReturn(new BigDecimal("120"));

        when(this.pricingService.calculateSellingPrice(
                new BigDecimal("50")
        )).thenReturn(new BigDecimal("60"));

        when(this.modelMapper.map(
                any(Order.class),
                eq(OrderDto.class)
        )).thenReturn(mock(OrderDto.class));

        this.orderService.createOrder(createOrderDto, email);

        ArgumentCaptor<Order> captor =
                ArgumentCaptor.forClass(Order.class);

        verify(this.orderRepository).save(captor.capture());

        Order savedOrder = captor.getValue();

        assertEquals(
                0,
                new BigDecimal("420").compareTo(savedOrder.getTotal())
        );

        assertEquals(2, savedOrder.getOrderItemList().size());

        verify(this.inventoryService).decreaseStock(1L, 2);
        verify(this.inventoryService).decreaseStock(2L, 3);
    }


    @Test
    void createOrder_ShouldThrowException_WhenItemListIsNull() {
        String email = "customer@test.com";

        CreateOrderDto createOrderDto = mock(CreateOrderDto.class);

        when(createOrderDto.getItems())
                .thenReturn(null);

        assertThrows(
                InvalidInputException.class,
                () -> this.orderService.createOrder(
                        createOrderDto,
                        email
                )
        );

        verify(this.orderRepository, never())
                .save(any(Order.class));

        verify(this.inventoryService, never())
                .decreaseStock(any(), any());
    }


    @Test
    void createOrder_ShouldThrowException_WhenItemListIsEmpty() {
        String email = "customer@test.com";

        CreateOrderDto createOrderDto = mock(CreateOrderDto.class);

        when(createOrderDto.getItems())
                .thenReturn(List.of());

        assertThrows(
                InvalidInputException.class,
                () -> this.orderService.createOrder(
                        createOrderDto,
                        email
                )
        );

        verify(this.orderRepository, never())
                .save(any(Order.class));

        verify(this.inventoryService, never())
                .decreaseStock(any(), any());
    }


    @Test
    void createOrder_ShouldThrowException_WhenProductDoesNotExist() {
        String email = "customer@test.com";

        CreateOrderDto createOrderDto = mock(CreateOrderDto.class);
        OrderItemDto itemDto = mock(OrderItemDto.class);

        when(createOrderDto.getItems())
                .thenReturn(List.of(itemDto));

        when(itemDto.getProductId())
                .thenReturn(999L);

        when(this.productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.orderService.createOrder(
                        createOrderDto,
                        email
                )
        );

        verify(this.inventoryService, never())
                .decreaseStock(any(), any());

        verify(this.orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void createOrder_ShouldThrowException_WhenQuantityIsNull() {
        String email = "customer@test.com";

        CreateOrderDto createOrderDto = mock(CreateOrderDto.class);
        OrderItemDto itemDto = mock(OrderItemDto.class);

        Product product = new Product();
        product.setId(1L);

        when(createOrderDto.getItems())
                .thenReturn(List.of(itemDto));

        when(itemDto.getProductId())
                .thenReturn(1L);

        when(itemDto.getQuantity())
                .thenReturn(null);

        when(this.productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InvalidQuantityException.class,
                () -> this.orderService.createOrder(
                        createOrderDto,
                        email
                )
        );

        verify(this.pricingService, never())
                .calculateSellingPrice(any());

        verify(this.inventoryService, never())
                .decreaseStock(any(), any());

        verify(this.orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void createOrder_ShouldThrowException_WhenQuantityIsZero() {
        String email = "customer@test.com";

        CreateOrderDto createOrderDto = mock(CreateOrderDto.class);
        OrderItemDto itemDto = mock(OrderItemDto.class);

        Product product = new Product();
        product.setId(1L);

        when(createOrderDto.getItems())
                .thenReturn(List.of(itemDto));

        when(itemDto.getProductId())
                .thenReturn(1L);

        when(itemDto.getQuantity())
                .thenReturn(0);

        when(this.productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InvalidQuantityException.class,
                () -> this.orderService.createOrder(
                        createOrderDto,
                        email
                )
        );

        verify(this.inventoryService, never())
                .decreaseStock(any(), any());

        verify(this.orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void createOrder_ShouldThrowException_WhenQuantityIsNegative() {
        String email = "customer@test.com";

        CreateOrderDto createOrderDto = mock(CreateOrderDto.class);
        OrderItemDto itemDto = mock(OrderItemDto.class);

        Product product = new Product();
        product.setId(1L);

        when(createOrderDto.getItems())
                .thenReturn(List.of(itemDto));

        when(itemDto.getProductId())
                .thenReturn(1L);

        when(itemDto.getQuantity())
                .thenReturn(-5);

        when(this.productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InvalidQuantityException.class,
                () -> this.orderService.createOrder(
                        createOrderDto,
                        email
                )
        );

        verify(this.inventoryService, never())
                .decreaseStock(any(), any());

        verify(this.orderRepository, never())
                .save(any(Order.class));
    }


    @Test
    void getOrderByIdEntity_ShouldReturnOrder_WhenOrderExists() {
        Long orderId = 1L;

        Order expected = new Order();

        when(this.orderRepository.findById(orderId))
                .thenReturn(Optional.of(expected));

        Order actual =
                this.orderService.getOrderByIdEntity(orderId);

        assertSame(expected, actual);
    }


    @Test
    void getOrderByIdEntity_ShouldThrowException_WhenOrderDoesNotExist() {
        Long orderId = 1L;

        when(this.orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.orderService.getOrderByIdEntity(orderId)
        );
    }


    @Test
    void markOrderAsPaid_ShouldChangeStatusToPaid_WhenOrderIsAwaitingPayment() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.AWAITING_PAYMENT);

        this.orderService.markOrderAsPaid(order);

        assertEquals(
                OrderStatus.PAID,
                order.getOrderStatus()
        );
    }


    @Test
    void markOrderAsPaid_ShouldNotChangeStatus_WhenOrderIsNotAwaitingPayment() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.CANCELLED);

        this.orderService.markOrderAsPaid(order);

        assertEquals(
                OrderStatus.CANCELLED,
                order.getOrderStatus()
        );
    }


    @Test
    void getOrderByIdEntityForUpdate_ShouldReturnOrder_WhenOrderExists() {
        Long orderId = 1L;

        Order expected = new Order();

        when(this.orderRepository.findByIdForUpdate(orderId))
                .thenReturn(Optional.of(expected));

        Order actual =
                this.orderService.getOrderByIdEntityForUpdate(orderId);

        assertSame(expected, actual);

        verify(this.orderRepository)
                .findByIdForUpdate(orderId);
    }


    @Test
    void getOrderByIdEntityForUpdate_ShouldThrowException_WhenOrderDoesNotExist() {
        Long orderId = 1L;

        when(this.orderRepository.findByIdForUpdate(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                ObjectNotFoundException.class,
                () -> this.orderService.getOrderByIdEntityForUpdate(orderId)
        );
    }
}