package com.example.demo.Model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    List<CartItem> findByUser(MyAppUser user);
    
    Optional<CartItem> findByUserAndProductAndSizeAndColor(MyAppUser user, Product product, String size, String color);
    
    void deleteByUser(MyAppUser user);
}