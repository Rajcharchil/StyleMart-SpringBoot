package com.example.demo.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Address;
import com.example.demo.Model.AddressRepository;
import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.MyAppUserRepository;

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
    
    // Endpoint for temporary address validation (no authentication required)
    @PostMapping("/validate")
    public ResponseEntity<String> validateAddress(@RequestBody Address address) {
        // Validate required fields
        if (address.getFullName() == null || address.getFullName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Full name is required");
        }
        if (address.getAddressLine1() == null || address.getAddressLine1().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Address line 1 is required");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("City is required");
        }
        if (address.getState() == null || address.getState().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("State is required");
        }
        if (address.getPincode() == null || address.getPincode().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Pincode is required");
        }
        if (address.getPhoneNumber() == null || address.getPhoneNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Phone number is required");
        }
        
        // Validate phone number format (basic validation)
        if (!address.getPhoneNumber().matches("\\d{10}")) {
            return ResponseEntity.badRequest().body("Phone number must be 10 digits");
        }
        
        // Validate pincode format (basic validation)
        if (!address.getPincode().matches("\\d{6}")) {
            return ResponseEntity.badRequest().body("Pincode must be 6 digits");
        }
        
        return ResponseEntity.ok("Address is valid");
    }
}