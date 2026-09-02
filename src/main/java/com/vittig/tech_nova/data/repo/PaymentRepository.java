package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Payment;
import com.vittig.tech_nova.data.util.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByOrderId(Long orderId);
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus=:status AND p.time<=:now")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Payment> findDuePayments(PaymentStatus status, LocalDateTime now);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
    Optional<Payment> findPaymentByOrderIdForUpdate(Long orderId);

    Optional<Payment> getPaymentByOrderId(Long orderId);
}
