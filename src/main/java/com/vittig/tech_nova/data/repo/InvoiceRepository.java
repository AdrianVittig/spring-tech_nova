package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByOrderId(Long orderId);

    @Query("SELECT i FROM Invoice i WHERE i.order.id = :orderId")
    Optional<Invoice> getInvoiceByOrderId(Long orderId);
}
