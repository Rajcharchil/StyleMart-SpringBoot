package com.example.demo.Controller;

import com.example.demo.Model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private MyAppUserRepository userRepository;
    
    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        MyAppUser user = userOpt.get();
        
        // Get cart items
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new CheckoutResponse("Cart is empty", null));
        }
        
        // Get address
        Optional<Address> addressOpt = addressRepository.findById(request.getAddressId());
        
        if (!addressOpt.isPresent() || !addressOpt.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new CheckoutResponse("Invalid address", null));
        }
        
        Address address = addressOpt.get();
        
        // Calculate total
        double totalAmount = cartItems.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus("PENDING");
        
        // Copy address details
        order.setShippingFullName(address.getFullName());
        order.setShippingAddressLine1(address.getAddressLine1());
        order.setShippingAddressLine2(address.getAddressLine2());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPincode(address.getPincode());
        order.setShippingPhoneNumber(address.getPhoneNumber());
        
        order = orderRepository.save(order);
        
        // Create order items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem(order, cartItem.getProduct(), cartItem.getQuantity(), 
                    cartItem.getSize(), cartItem.getColor());
            order.getOrderItems().add(orderItem);
        }
        
        orderRepository.save(order);
        
        // Clear cart
        cartItemRepository.deleteByUser(user);
        
        return ResponseEntity.ok(new CheckoutResponse("Order placed successfully", order.getId()));
    }
    
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Order> orders = orderRepository.findByUserOrderByCreatedAtDesc(userOpt.get());
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId, Authentication authentication) {
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
        
        if (!order.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(order);
    }
    
    // Request and Response DTOs
    public static class CheckoutRequest {
        private Long addressId;
        private String paymentMethod;
        
        public Long getAddressId() { return addressId; }
        public void setAddressId(Long addressId) { this.addressId = addressId; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
    
    public static class CheckoutResponse {
        private String message;
        private Long orderId;
        
        public CheckoutResponse(String message, Long orderId) {
            this.message = message;
            this.orderId = orderId;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
    }
}