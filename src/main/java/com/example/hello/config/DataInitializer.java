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

            try {
                if (userService.findByUsername("admin").isEmpty()) {
                    userService.registerUser("admin", "admin123", "admin", "ADMIN", null);
                    System.out.println("Admin user created: admin / admin123");
                }
            } catch (Exception e) {
                System.out.println("Admin user creation skipped or failed: " + e.getMessage());
            }

            try {
                if (userService.findByUsername("seller").isEmpty()) {
                    userService.registerUser("seller", "seller123", "Test Seller", "SELLER",
                            "http://example.com/identity.jpg");
                    System.out.println("Seller user created: seller / seller123");
                }
            } catch (Exception e) {
                System.out.println("Seller user creation skipped or failed: " + e.getMessage());
            }

            try {
                if (userService.findByUsername("user").isEmpty()) {
                    userService.registerUser("user", "user123", "Test User", "USER", null);
                    System.out.println("Standard user created: user / user123");
                }
            } catch (Exception e) {
                System.out.println("Standard user creation skipped or failed: " + e.getMessage());
            }
        };
    }
}
