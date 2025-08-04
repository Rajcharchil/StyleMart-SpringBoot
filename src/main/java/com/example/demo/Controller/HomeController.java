package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/female")
    public String female() {
        return "female";
    }
    
    @GetMapping("/kids")
    public String kids() {
        return "kids";
    }
} 