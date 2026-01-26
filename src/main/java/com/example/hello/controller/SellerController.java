package com.example.hello.controller;

import com.example.hello.model.Product;
import com.example.hello.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final ProductService productService;
    private final com.example.hello.user.UserService userService;
    private final com.example.hello.service.CategoryService categoryService;
    private final com.example.hello.service.FileStorageService fileStorageService;

    public SellerController(ProductService productService, com.example.hello.user.UserService userService, com.example.hello.service.CategoryService categoryService, com.example.hello.service.FileStorageService fileStorageService) {
        this.productService = productService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "seller/dashboard";
    }

    @PostMapping("/add-product")
    public String addProduct(@ModelAttribute Product product, 
                           @org.springframework.web.bind.annotation.RequestParam("image") org.springframework.web.multipart.MultipartFile imageFile,
                           java.security.Principal principal) {
        String username = principal.getName();
        com.example.hello.user.User user = userService.findByUsername(username).orElseThrow();
        product.setSeller(user); // Set the logged-in user as the seller
        
        // Handle image upload
        if (!imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(imageFile);
            product.setImageUrl(imageUrl);
        }
        
        productService.saveProduct(product);
        return "redirect:/seller/dashboard?success";
    }

    @GetMapping("/my-products")
    public String myProducts(Model model, java.security.Principal principal) {
        String username = principal.getName();
        com.example.hello.user.User user = userService.findByUsername(username).orElseThrow();
        model.addAttribute("products", productService.getProductsBySeller(user));
        return "seller/my-products";
    }

    @GetMapping("/edit-product/{id}")
    public String editProduct(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).orElseThrow();
        // In a real app, check if the current user owns this product
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "seller/edit-product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Product product,
                              @org.springframework.web.bind.annotation.RequestParam(value = "image", required = false) org.springframework.web.multipart.MultipartFile imageFile) {
        // Handle image upload if a new image is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(imageFile);
            product.setImageUrl(imageUrl);
        }
        
        productService.updateProduct(product);
        return "redirect:/seller/my-products?updated";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@org.springframework.web.bind.annotation.PathVariable Long id) {
        // In a real app, check if the current user owns this product
        productService.deleteProduct(id);
        return "redirect:/seller/my-products?deleted";
    }
}
