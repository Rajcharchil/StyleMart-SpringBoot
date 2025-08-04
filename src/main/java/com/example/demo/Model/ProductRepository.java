package com.example.demo.Model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByActiveTrue();
    
    List<Product> findByActiveTrueAndCategory(String category);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findByActiveTrueAndNameOrDescriptionContainingIgnoreCase(@Param("search") String search);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.category = :category AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findByActiveTrueAndCategoryAndNameOrDescriptionContainingIgnoreCase(
            @Param("category") String category, @Param("search") String search);
    
    // Get products by gender category
    List<Product> findByActiveTrueAndCategoryIn(List<String> categories);
}