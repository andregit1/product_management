package com.example.product_management.repository;

import com.example.product_management.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(Product.Status status);

    // Method to find all products regardless of status
    List<Product> findAll();
}
