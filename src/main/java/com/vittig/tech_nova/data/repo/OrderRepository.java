package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
