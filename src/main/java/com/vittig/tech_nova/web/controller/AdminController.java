package com.vittig.tech_nova.web.controller;

import com.vittig.tech_nova.data.dto.budget.BudgetDto;
import com.vittig.tech_nova.data.dto.inventory.InventoryDto;
import com.vittig.tech_nova.data.dto.invoice.InvoiceDto;
import com.vittig.tech_nova.data.dto.order.OrderDto;
import com.vittig.tech_nova.data.dto.payment.PaymentDto;
import com.vittig.tech_nova.data.dto.product.CreateProductDto;
import com.vittig.tech_nova.data.dto.product.ProductDto;
import com.vittig.tech_nova.data.dto.transaction.FTDto;
import com.vittig.tech_nova.service.contract.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ProductService productService;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final FinancialTransactionService financialTransactionService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;
    private final BudgetService budgetService;

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

    @GetMapping("/inventory/{productId}")
    public InventoryDto getInventoryByProductId(@PathVariable Long productId){
        return this.inventoryService.getInventoryByProductId(productId);
    }

    @GetMapping("/transactions")
    public List<FTDto> getAllTransactions(){
        return this.financialTransactionService.getAllTransactions();
    }

    @GetMapping("/transactions/{id}")
    public FTDto getTransactionById(@PathVariable Long id){
        return this.financialTransactionService.getTransactionById(id);
    }

    @GetMapping("/invoices")
    public List<InvoiceDto> getAllInvoices(){
        return this.invoiceService.getAllInvoices();
    }

    @GetMapping("/invoices/{id}")
    public InvoiceDto getInvoiceById(@PathVariable Long id){
        return this.invoiceService.getInvoiceById(id);
    }

    @GetMapping("/payments")
    public List<PaymentDto> getAllPayments(){
        return this.paymentService.getAllPayments();
    }

    @GetMapping("/payments/{id}")
    public PaymentDto getPaymentById(@PathVariable Long id){
        return this.paymentService.getPaymentById(id);
    }

    @GetMapping("/budget")
    public BigDecimal getBudget(){
        return this.budgetService.getBalance();
    }
}
