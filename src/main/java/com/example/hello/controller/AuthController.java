package com.example.hello.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.hello.service.FileStorageService;
import com.example.hello.user.UserService;

@Controller
public class AuthController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    public AuthController(UserService userService, FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String role,
            @RequestParam(required = false) MultipartFile identityImage,
            Model model) {
        try {
            String identityImageUrl = null;

            if ("SELLER".equals(role) && identityImage != null && !identityImage.isEmpty()) {
                identityImageUrl = fileStorageService.storeIdFile(identityImage);
            }

            userService.registerUser(username, password, fullName, role, identityImageUrl);

            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
