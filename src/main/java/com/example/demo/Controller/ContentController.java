package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    
    @GetMapping("/req/login")
    public String login(){
        return "login";
    }
    
    @GetMapping("/req/signup")
    public String signup(){
        return "signup";
    }
    @GetMapping("/index")
    public String home(){
        return "index";
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
    
    @GetMapping("/products-page")
    public String products() {
        return "products";
    }
    
    @GetMapping("/cart-page")
    public String cart() {
        return "cart";
    }
    
    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }
    
    @GetMapping("/orders-page")
    public String orders() {
        return "orders";
    }
    
    @GetMapping("/order-success")
    public String orderSuccess() {
        return "order-success";
    }
}
