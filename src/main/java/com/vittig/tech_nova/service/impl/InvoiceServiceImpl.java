package com.vittig.tech_nova.service.impl;

import com.vittig.tech_nova.data.dto.invoice.InvoiceDto;
import com.vittig.tech_nova.data.entity.Invoice;
import com.vittig.tech_nova.data.entity.Order;
import com.vittig.tech_nova.data.repo.InvoiceRepository;
import com.vittig.tech_nova.data.util.ModelMapperUtil;
import com.vittig.tech_nova.data.util.OrderStatus;
import com.vittig.tech_nova.service.contract.InvoiceService;
import com.vittig.tech_nova.service.contract.OrderService;
import com.vittig.tech_nova.service.exception.ConflictException;
import com.vittig.tech_nova.service.exception.ForbiddenOperationException;
import com.vittig.tech_nova.service.exception.InvalidStatusException;
import com.vittig.tech_nova.service.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final ModelMapperUtil modelMapper;
    private final OrderService orderService;

    @Override
    public List<InvoiceDto> getAllInvoices() {
        return modelMapper.mapList(this.invoiceRepository.findAll(), InvoiceDto.class);
    }

    @Override
    public InvoiceDto getInvoiceById(Long id) {
        return modelMapper.map(this.invoiceRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Invoice not found.")
        ), InvoiceDto.class);
    }

    @Override
    @Transactional
    public InvoiceDto createInvoiceForOrder(Long orderId) {
        Order order = this.orderService.getOrderByIdEntity(orderId);
        Invoice invoice = new Invoice();
        if(order.getOrderStatus() != OrderStatus.PAID){
            throw new InvalidStatusException("Order must be paid before an invoice can be created.");
        }
        if(this.invoiceRepository.existsByOrderId(orderId)){
            throw new ConflictException("An invoice already exists for this order.");
        }
        BigDecimal total = order.getTotal();
        invoice.setIssuedAt(LocalDateTime.now());
        invoice.setOrder(order);
        order.setInvoice(invoice);
        invoice.setTotalAmount(total);
        this.invoiceRepository.save(invoice);
        invoice.setInvoiceNumber(invoice.getId());
        return modelMapper.map(invoice, InvoiceDto.class);
    }

    @Override
    public InvoiceDto getInvoiceForOrder(Long orderId, String email) {
        Order order = this.orderService.getOrderByIdEntity(orderId);
        if(!Objects.equals(order.getUser().getEmail(), email)){
            throw new ForbiddenOperationException("You do not have permission to access this invoice.");
        }
        return modelMapper.map(this.invoiceRepository.getInvoiceByOrderId(orderId).orElseThrow(
                () -> new ObjectNotFoundException("Invoice not found.")
        ), InvoiceDto.class);
    }
}
