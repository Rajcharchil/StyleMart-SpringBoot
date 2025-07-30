package com.example.demo.service;

import com.example.demo.Model.Product;
import com.example.demo.Model.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize sample products if database is empty
        if (productRepository.count() == 0) {
            initializeSampleProducts();
        }
    }
    
    private void initializeSampleProducts() {
        // Men's Clothing
        Product menShirt = new Product("Classic Cotton Shirt", 1299.0, 50, "Men", "Premium quality cotton shirt perfect for formal and casual occasions");
        menShirt.setImages(Arrays.asList("https://images.pexels.com/photos/996329/pexels-photo-996329.jpeg"));
        menShirt.setRating(4.2);
        menShirt.setReviewCount(45);
        
        Product menJeans = new Product("Slim Fit Jeans", 1899.0, 30, "Men", "Comfortable slim fit jeans made from premium denim");
        menJeans.setImages(Arrays.asList("https://images.pexels.com/photos/1598507/pexels-photo-1598507.jpeg"));
        menJeans.setRating(4.5);
        menJeans.setReviewCount(67);
        
        // Women's Clothing
        Product womenDress = new Product("Floral Summer Dress", 2199.0, 25, "Women", "Beautiful floral print dress perfect for summer occasions");
        womenDress.setImages(Arrays.asList("https://images.pexels.com/photos/1536619/pexels-photo-1536619.jpeg"));
        womenDress.setRating(4.7);
        womenDress.setReviewCount(89);
        
        Product womenTop = new Product("Casual Cotton Top", 899.0, 40, "Women", "Comfortable cotton top for everyday wear");
        womenTop.setImages(Arrays.asList("https://images.pexels.com/photos/1040945/pexels-photo-1040945.jpeg"));
        womenTop.setRating(4.1);
        womenTop.setReviewCount(34);
        
        // Kids Clothing
        Product kidsShirt = new Product("Kids Cartoon T-Shirt", 599.0, 60, "Kids", "Fun cartoon printed t-shirt for kids");
        kidsShirt.setImages(Arrays.asList("https://images.pexels.com/photos/1620760/pexels-photo-1620760.jpeg"));
        kidsShirt.setRating(4.3);
        kidsShirt.setReviewCount(23);
        
        // Electronics
        Product watch = new Product("Smart Fitness Watch", 3999.0, 15, "Electronics", "Advanced fitness tracking smartwatch with heart rate monitor");
        watch.setImages(Arrays.asList("https://images.pexels.com/photos/437037/pexels-photo-437037.jpeg"));
        watch.setRating(4.6);
        watch.setReviewCount(156);
        
        Product headphones = new Product("Wireless Headphones", 2499.0, 20, "Electronics", "Premium wireless headphones with noise cancellation");
        headphones.setImages(Arrays.asList("https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg"));
        headphones.setRating(4.4);
        headphones.setReviewCount(78);
        
        // Home & Living
        Product cushion = new Product("Decorative Cushion Cover", 799.0, 35, "Home & Living", "Beautiful decorative cushion cover to enhance your home decor");
        cushion.setImages(Arrays.asList("https://images.pexels.com/photos/1571460/pexels-photo-1571460.jpeg"));
        cushion.setRating(4.0);
        cushion.setReviewCount(12);
        
        // Beauty
        Product skincare = new Product("Natural Face Cream", 1299.0, 45, "Beauty", "Organic face cream with natural ingredients for healthy skin");
        skincare.setImages(Arrays.asList("https://images.pexels.com/photos/3685530/pexels-photo-3685530.jpeg"));
        skincare.setRating(4.8);
        skincare.setReviewCount(234);
        
        // Save all products
        productRepository.saveAll(Arrays.asList(
            menShirt, menJeans, womenDress, womenTop, kidsShirt, 
            watch, headphones, cushion, skincare
        ));
        
        System.out.println("Sample products initialized successfully!");
    }
}