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
@RequestMapping("/addresses")
public class AddressController {
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private MyAppUserRepository userRepository;
    
    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<Address> addresses = addressRepository.findByUser(userOpt.get());
        return ResponseEntity.ok(addresses);
    }
    
    @PostMapping
    public ResponseEntity<String> addAddress(@RequestBody Address address, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login");
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        
        MyAppUser user = userOpt.get();
        address.setUser(user);
        
        // If this is the first address or marked as default, make it default
        List<Address> existingAddresses = addressRepository.findByUser(user);
        if (existingAddresses.isEmpty() || address.getIsDefault()) {
            // Remove default from other addresses
            existingAddresses.forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
            address.setIsDefault(true);
        }
        
        addressRepository.save(address);
        return ResponseEntity.ok("Address added successfully");
    }
    
    @PutMapping("/{addressId}")
    public ResponseEntity<String> updateAddress(@PathVariable Long addressId, @RequestBody Address updatedAddress, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login");
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        
        Optional<Address> addressOpt = addressRepository.findById(addressId);
        
        if (!addressOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Address address = addressOpt.get();
        
        if (!address.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        // Update address fields
        address.setFullName(updatedAddress.getFullName());
        address.setAddressLine1(updatedAddress.getAddressLine1());
        address.setAddressLine2(updatedAddress.getAddressLine2());
        address.setCity(updatedAddress.getCity());
        address.setState(updatedAddress.getState());
        address.setPincode(updatedAddress.getPincode());
        address.setPhoneNumber(updatedAddress.getPhoneNumber());
        
        // Handle default address
        if (updatedAddress.getIsDefault()) {
            List<Address> userAddresses = addressRepository.findByUser(userOpt.get());
            userAddresses.forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
            address.setIsDefault(true);
        }
        
        addressRepository.save(address);
        return ResponseEntity.ok("Address updated successfully");
    }
    
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login");
        }
        
        String username = authentication.getName();
        Optional<MyAppUser> userOpt = userRepository.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        
        Optional<Address> addressOpt = addressRepository.findById(addressId);
        
        if (!addressOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Address address = addressOpt.get();
        
        if (!address.getUser().getId().equals(userOpt.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        
        addressRepository.delete(address);
        return ResponseEntity.ok("Address deleted successfully");
    }
}