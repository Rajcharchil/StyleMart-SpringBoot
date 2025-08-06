package com.example.demo.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Address;
import com.example.demo.Model.AddressRepository;
import com.example.demo.Model.CartItem;
import com.example.demo.Model.CartItemRepository;
import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.MyAppUserRepository;
import com.example.demo.Model.Order;
import com.example.demo.Model.OrderItem;
import com.example.demo.Model.OrderItemRepository;
import com.example.demo.Model.OrderRepository;

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
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
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
        
        // Set estimated delivery date (3-5 business days from now)
        LocalDateTime estimatedDelivery = LocalDateTime.now().plusDays(4); // 4 days for 3-5 business days
        order.setEstimatedDeliveryDate(estimatedDelivery);
        
        // Copy address details
        order.setShippingFullName(address.getFullName());
        order.setShippingAddressLine1(address.getAddressLine1());
        order.setShippingAddressLine2(address.getAddressLine2());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPincode(address.getPincode());
        order.setShippingPhoneNumber(address.getPhoneNumber());
        
        order = orderRepository.save(order);
        
        // Create and save order items
        System.out.println("Creating order items for " + cartItems.size() + " cart items");
        for (CartItem cartItem : cartItems) {
            System.out.println("Creating order item for product: " + cartItem.getProduct().getName());
            OrderItem orderItem = new OrderItem(order, cartItem.getProduct(), cartItem.getQuantity(), 
                    cartItem.getSize(), cartItem.getColor());
            orderItem = orderItemRepository.save(orderItem);
            order.getOrderItems().add(orderItem);
            System.out.println("Saved order item with ID: " + orderItem.getId());
        }
        
        // Verify order items were saved
        System.out.println("Total order items created: " + order.getOrderItems().size());
        if (order.getOrderItems().isEmpty()) {
            return ResponseEntity.badRequest().body(new CheckoutResponse("Failed to create order items", null));
        }
        
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
    
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, 
                                                 @RequestBody OrderStatusUpdateRequest request,
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
        
        if (!order.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Update order status
        order.setStatus(request.getStatus());
        
        // Update tracking information based on status
        if ("SHIPPED".equals(request.getStatus())) {
            order.setShippedDate(LocalDateTime.now());
            order.setTrackingNumber(request.getTrackingNumber());
            order.setCourierName(request.getCourierName());
        } else if ("DELIVERED".equals(request.getStatus())) {
            order.setDeliveredDate(LocalDateTime.now());
        }
        
        orderRepository.save(order);
        
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/debug/{orderId}")
    public ResponseEntity<Order> debugOrder(@PathVariable Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            System.out.println("Debug - Order: " + order.getOrderNumber());
            System.out.println("Debug - Items count: " + order.getOrderItems().size());
            
            // Force load order items
            order.getOrderItems().forEach(item -> {
                System.out.println("Debug - Item: " + item.getProductName() + ", Qty: " + item.getQuantity());
            });
            
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/test-create")
    public ResponseEntity<String> testCreateOrder() {
        try {
            // This is just for testing - in real app you'd get user from authentication
            Optional<MyAppUser> userOpt = userRepository.findByUsername("testuser");
            if (!userOpt.isPresent()) {
                return ResponseEntity.ok("Test user not found. Please create a user first.");
            }
            
            MyAppUser user = userOpt.get();
            
            // Create a test order
            Order order = new Order();
            order.setUser(user);
            order.setOrderNumber("TEST-" + System.currentTimeMillis());
            order.setTotalAmount(1000.0);
            order.setStatus("PENDING");
            order.setPaymentMethod("CASH");
            order.setPaymentStatus("PENDING");
            order.setEstimatedDeliveryDate(LocalDateTime.now().plusDays(4));
            
            // Set dummy address
            order.setShippingFullName("Test User");
            order.setShippingAddressLine1("123 Test Street");
            order.setShippingCity("Test City");
            order.setShippingState("Test State");
            order.setShippingPincode("123456");
            order.setShippingPhoneNumber("1234567890");
            
            order = orderRepository.save(order);
            
            // Create a test order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductName("Test Product");
            orderItem.setProductImage("/assest/logo.jpg");
            orderItem.setQuantity(2);
            orderItem.setPrice(500.0);
            orderItem.setSize("M");
            orderItem.setColor("Blue");
            
            orderItemRepository.save(orderItem);
            
            return ResponseEntity.ok("Test order created with ID: " + order.getId());
            
        } catch (Exception e) {
            return ResponseEntity.ok("Error creating test order: " + e.getMessage());
        }
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
    
    public static class OrderStatusUpdateRequest {
        private String status;
        private String trackingNumber;
        private String courierName;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        
        public String getCourierName() { return courierName; }
        public void setCourierName(String courierName) { this.courierName = courierName; }
    }
}