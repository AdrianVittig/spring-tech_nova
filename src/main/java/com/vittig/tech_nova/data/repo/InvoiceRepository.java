package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByOrderId(Long orderId);
}
