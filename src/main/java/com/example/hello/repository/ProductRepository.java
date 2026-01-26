package com.example.hello.repository;

import com.example.hello.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    java.util.List<Product> findBySeller(com.example.hello.user.User seller);
}
