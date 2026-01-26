package com.example.hello.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.hello.user.UserService;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserService userService) {
        return args -> {
            // Create Admin
            try {
                if (userService.findByUsername("admin").isEmpty()) {
                    userService.registerUser("admin", "admin123", "System Admin", "ADMIN");
                    System.out.println("Admin user created: admin / admin123");
                }
            } catch (Exception e) {
                System.out.println("Admin user creation skipped or failed: " + e.getMessage());
            }

            // Create Seller
            try {
                if (userService.findByUsername("seller").isEmpty()) {
                    userService.registerUser("seller", "seller123", "Test Seller", "SELLER");
                    System.out.println("Seller user created: seller / seller123");
                }
            } catch (Exception e) {
                System.out.println("Seller user creation skipped or failed: " + e.getMessage());
            }

            // Create User
            try {
                if (userService.findByUsername("user").isEmpty()) {
                    userService.registerUser("user", "user123", "Test User", "USER");
                    System.out.println("Standard user created: user / user123");
                }
            } catch (Exception e) {
                System.out.println("Standard user creation skipped or failed: " + e.getMessage());
            }
        };
    }
}
