package com.vittig.tech_nova.data.repo;

import com.vittig.tech_nova.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
