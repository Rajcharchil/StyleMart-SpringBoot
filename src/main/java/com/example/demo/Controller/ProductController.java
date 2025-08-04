package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Product;
import com.example.demo.Model.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        
        List<Product> products;
        
        if (search != null && !search.trim().isEmpty() && category != null && !category.trim().isEmpty()) {
            products = productRepository.findByActiveTrueAndCategoryAndNameOrDescriptionContainingIgnoreCase(category, search);
        } else if (search != null && !search.trim().isEmpty()) {
            products = productRepository.findByActiveTrueAndNameOrDescriptionContainingIgnoreCase(search);
        } else if (category != null && !category.trim().isEmpty()) {
            products = productRepository.findByActiveTrueAndCategory(category);
        } else {
            products = productRepository.findByActiveTrue();
        }
        
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        
        if (product.isPresent() && product.get().getActive()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/women")
    public ResponseEntity<List<Product>> getWomenProducts() {
        List<Product> womenProducts = productRepository.findByActiveTrueAndCategory("Women");
        return ResponseEntity.ok(womenProducts);
    }
    
    @GetMapping("/men")
    public ResponseEntity<List<Product>> getMenProducts() {
        List<Product> menProducts = productRepository.findByActiveTrueAndCategory("Men");
        return ResponseEntity.ok(menProducts);
    }
    
    @GetMapping("/debug/all")
    public ResponseEntity<List<Product>> getAllProductsDebug() {
        List<Product> allProducts = productRepository.findByActiveTrue();
        return ResponseEntity.ok(allProducts);
    }
}