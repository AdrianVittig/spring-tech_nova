package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderId(Long orderId);
}
