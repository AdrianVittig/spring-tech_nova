package com.vittig.tech_nova.service.contract;

import com.vittig.tech_nova.data.dto.invoice.InvoiceDto;

import java.util.List;

public interface InvoiceService {
    List<InvoiceDto> getAllInvoices();

    InvoiceDto getInvoiceById(Long id);

    InvoiceDto createInvoiceForOrder(Long orderId);

    InvoiceDto getInvoiceForOrder(Long orderId, String email);
}
