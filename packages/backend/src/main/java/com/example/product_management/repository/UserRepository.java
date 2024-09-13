package com.example.product_management.repository;

import com.example.product_management.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query to find by username
    Optional<User> findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
}
