package com.example.blogs.controllers;

import com.example.blogs.Services.JpaUserService;
import com.example.blogs.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final JpaUserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(JpaUserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, 
                          @RequestParam String email, 
                          @RequestParam String code,
                          RedirectAttributes redirectAttributes) {
        
        // Check if email already exists
        if (userService.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Email already exists. Please use a different email.");
            return "redirect:/auth/login#register";
        }
        
        // Create new user with encoded password
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(code));
        
        // Set default role - set as ADMIN for the admin user
        if ("admin".equals(email)) {
            newUser.setRole("ADMIN");
        } else {
            newUser.setRole("USER");
        }
        
        try {
            userService.createUser(newUser);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error during registration: " + e.getMessage());
            return "redirect:/auth/login#register";
        }
    }

    @GetMapping("/welcome")
    public String showWelcomePage() {
        // Spring Security handles authentication checking
        return "welcome";
    }
}
