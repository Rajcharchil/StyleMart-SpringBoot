package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.MyAppUserRepository;
import com.example.demo.Model.Order;
import com.example.demo.Model.OrderRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private MyAppUserRepository userRepository;
    
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // For now, allow any authenticated user to access admin functions
        // In a real application, you would check for admin role
        List<Order> orders = orderRepository.findAllWithOrderItems();
        return ResponseEntity.ok(orders);
    }
    
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, 
                                                 @RequestBody OrderController.OrderStatusUpdateRequest request,
                                                 Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        
        if (!orderOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Order order = orderOpt.get();
        
        // Update order status
        order.setStatus(request.getStatus());
        
        // Update tracking information based on status
        if ("SHIPPED".equals(request.getStatus())) {
            order.setShippedDate(java.time.LocalDateTime.now());
            order.setTrackingNumber(request.getTrackingNumber());
            order.setCourierName(request.getCourierName());
        } else if ("DELIVERED".equals(request.getStatus())) {
            order.setDeliveredDate(java.time.LocalDateTime.now());
        }
        
        orderRepository.save(order);
        
        return ResponseEntity.ok(order);
    }
} 