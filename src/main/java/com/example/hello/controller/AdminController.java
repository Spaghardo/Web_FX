package com.example.hello.controller;

import com.example.hello.model.Category;
import com.example.hello.service.CategoryService;
import com.example.hello.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final com.example.hello.user.UserService userService;

    public AdminController(CategoryService categoryService, ProductService productService, com.example.hello.user.UserService userService) {
        this.categoryService = categoryService;
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("category", new Category());
        return "admin/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/categories?success";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products?deleted";
    }

    @GetMapping("/sellers")
    public String sellers(Model model) {
        model.addAttribute("sellers", userService.getAllSellers());
        model.addAttribute("pendingSellers", userService.getPendingSellers());
        return "admin/sellers";
    }

    @PostMapping("/sellers/delete/{id}")
    public String deleteSeller(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/sellers?deleted";
    }

    @PostMapping("/sellers/approve/{id}")
    public String approveSeller(@PathVariable Long id) {
        try {
            userService.approveSeller(id);
            return "redirect:/admin/sellers?approved";
        } catch (Exception e) {
            return "redirect:/admin/sellers?error";
        }
    }

    @PostMapping("/sellers/reject/{id}")
    public String rejectSeller(@PathVariable Long id) {
        try {
            userService.rejectSeller(id);
            return "redirect:/admin/sellers?rejected";
        } catch (Exception e) {
            return "redirect:/admin/sellers?error";
        }
    }
}
