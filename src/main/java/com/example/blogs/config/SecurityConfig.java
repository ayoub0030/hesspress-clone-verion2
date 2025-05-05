package com.example.blogs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                // Public resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon/**").permitAll()
                // Public pages
                .requestMatchers("/", "/auth/login", "/auth/register").permitAll()
                .requestMatchers("/posts/view/**").permitAll()
                // API endpoints (adjust as needed)
                .requestMatchers("/api/**").permitAll()
                // Admin-only pages
                .requestMatchers("/admin/**", "/users/**", "/comments/**", "/analytics/**", "/settings/**").hasRole("ADMIN")
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/user/home")
                .failureUrl("/auth/login?error")
                .usernameParameter("email")
                .passwordParameter("code")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Disable CSRF for development (enable for production)
            .csrf(csrf -> csrf.disable());
            
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
