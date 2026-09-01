package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.id = :orderId")
    Optional<Order> findByIdForUpdate(Long orderId);

    @Query("SELECT o FROM Order o WHERE o.user.email = :email")
    List<Order> getAllOrdersByUserEmail(String email);
}
