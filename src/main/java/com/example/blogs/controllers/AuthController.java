package com.example.blogs.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.blogs.dto.UserDto;
import com.example.blogs.services.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    public AuthController() {
        
    }

    // handler method to handle user registration form submit request
    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("user") UserDto userDto) {
        try {
            // Check if username already exists
            if (userService.existsByEmail(userDto.getEmail())) {
                return "redirect:/register?error=emailExists";
            }
            
            // Create new user
            userService.save(userDto);
            
            // Redirect to login page with success message
            return "redirect:/login?success";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            
            // Redirect to registration page with error message
            return "redirect:/register?error";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        // Check if the user is already authenticated and it's not an anonymous user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && 
            !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/posts";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
}