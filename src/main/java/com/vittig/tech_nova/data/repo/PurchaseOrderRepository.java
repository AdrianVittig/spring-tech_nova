package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.PurchaseOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PurchaseOrder> getPurchaseOrderByIdForUpdate(Long id);
}
