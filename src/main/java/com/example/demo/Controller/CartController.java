package com.example.demo.Controller;

import com.example.demo.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private MyAppUserRepository userRepository;
    
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody AddToCartRequest request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login to add items to cart");
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        
        MyAppUser user = userOpt.get();
        Optional<Product> productOpt = productRepository.findById(request.getProductId());
        
        if (!productOpt.isPresent() || !productOpt.get().getActive()) {
            return ResponseEntity.badRequest().body("Product not found");
        }
        
        Product product = productOpt.get();
        
        if (product.getStock() < request.getQuantity()) {
            return ResponseEntity.badRequest().body("Insufficient stock");
        }
        
        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProductAndSizeAndColor(
                user, product, request.getSize(), request.getColor());
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            
            if (product.getStock() < newQuantity) {
                return ResponseEntity.badRequest().body("Insufficient stock for requested quantity");
            }
            
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(user, product, request.getQuantity(), request.getSize(), request.getColor());
            cartItemRepository.save(newItem);
        }
        
        return ResponseEntity.ok("Item added to cart successfully");
    }
    
    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<CartItem> cartItems = cartItemRepository.findByUser(userOpt.get());
        return ResponseEntity.ok(cartItems);
    }
    
    @PutMapping("/update/{itemId}")
    public ResponseEntity<String> updateCartItem(@PathVariable Long itemId, @RequestBody UpdateCartRequest request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login");
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(itemId);
        
        if (!cartItemOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        CartItem cartItem = cartItemOpt.get();
        
        if (!cartItem.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
            return ResponseEntity.ok("Item removed from cart");
        }
        
        if (cartItem.getProduct().getStock() < request.getQuantity()) {
            return ResponseEntity.badRequest().body("Insufficient stock");
        }
        
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        
        return ResponseEntity.ok("Cart updated successfully");
    }
    
    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long itemId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login");
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        
        Optional<CartItem> cartItemOpt = cartItemRepository.findById(itemId);
        
        if (!cartItemOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        CartItem cartItem = cartItemOpt.get();
        
        if (!cartItem.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        cartItemRepository.delete(cartItem);
        return ResponseEntity.ok("Item removed from cart");
    }
    
    // Request DTOs
    public static class AddToCartRequest {
        private Long productId;
        private Integer quantity;
        private String size;
        private String color;
        
        // Getters and Setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public String getSize() { return size; }
        public void setSize(String size) { this.size = size; }
        
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
    
    public static class UpdateCartRequest {
        private Integer quantity;
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}