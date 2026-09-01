package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Refund;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    List<Refund> findAllRefundsByOrderId(Long orderId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Refund r WHERE r.id = :refundId")
    Optional<Refund> findRefundByIdEntityForUpdate(Long refundId);
}
