package com.example.hello.controller;

import com.example.hello.favorite.Favorite;
import com.example.hello.favorite.FavoriteService;
import com.example.hello.model.Product;
import com.example.hello.service.ProductService;
import com.example.hello.user.User;
import com.example.hello.user.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final ProductService productService;
    private final FavoriteService favoriteService;
    private final UserService userService;

    public HomeController(ProductService productService, FavoriteService favoriteService, UserService userService) {
        this.productService = productService;
        this.favoriteService = favoriteService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        List<String> categories = products.stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);

        if (userDetails != null) {
            User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                List<Product> favoriteProducts = favoriteService.getUserFavorites(user).stream()
                        .map(Favorite::getProduct)
                        .limit(6)
                        .collect(Collectors.toList());
                model.addAttribute("favoriteProducts", favoriteProducts);
            }
        }

        return "home";
    }

    @GetMapping("/products")
    public String search(@RequestParam(required = false) String query, @RequestParam(required = false) String category,
            Model model) {
        List<Product> products = productService.getAllProducts();
        List<Product> filtered = products;
        if (query != null && !query.isEmpty()) {
            filtered = filtered.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (category != null && !category.isEmpty()) {
            filtered = filtered.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        List<String> categories = products.stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
        model.addAttribute("categories", categories);

        model.addAttribute("products", filtered);
        return "product-list";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).orElse(null);
        model.addAttribute("product", product);
        return "product-detail";
    }
}
