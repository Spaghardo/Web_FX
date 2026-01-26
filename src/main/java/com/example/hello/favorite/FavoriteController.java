



package com.example.hello.favorite;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.hello.model.Product;
import com.example.hello.service.ProductService;
import com.example.hello.user.User;
import com.example.hello.user.UserService;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String viewFavorites(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }
        List<Favorite> favorites = favoriteService.getUserFavorites(user);
        List<Product> favoriteProducts = favorites.stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
        
        model.addAttribute("products", favoriteProducts);
        return "favorites";
    }

    @PostMapping("/toggle")
    @ResponseBody
    public ResponseEntity<?> toggleFavorite(@RequestBody Map<String, Long> payload, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not logged in"));
        }

        Long productId = payload.get("productId");
        if (productId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Product ID is required"));
        }

        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }
        
        Product product = productService.getProductById(productId).orElse(null);

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        boolean isFavorite = favoriteService.isFavorite(user, product);
        if (isFavorite) {
            favoriteService.removeFavorite(user, product);
            return ResponseEntity.ok(Map.of("status", "removed"));
        } else {
            favoriteService.addFavorite(user, product);
            return ResponseEntity.ok(Map.of("status", "added"));
        }
    }

    @GetMapping("/ids")
    @ResponseBody
    public ResponseEntity<List<Long>> getFavoriteProductIds(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(List.of());
        }
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        List<Long> productIds = favoriteService.getUserFavorites(user).stream()
                .map(f -> f.getProduct().getId())
                .collect(Collectors.toList());
        return ResponseEntity.ok(productIds);
    }
    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<Product>> getFavoriteProducts(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.ok(List.of());
        }
        User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        List<Product> favoriteProducts = favoriteService.getUserFavorites(user).stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favoriteProducts);
    }
}
