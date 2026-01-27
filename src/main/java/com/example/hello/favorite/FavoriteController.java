package com.example.hello.favorite;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    private static final Logger logger = LoggerFactory.getLogger(FavoriteController.class);

    @GetMapping
    public String viewFavorites(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
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
    public ResponseEntity<?> toggleFavorite(@RequestBody Map<String, Long> payload,
            Principal principal) {

        logger.info("Toggle favorite called with payload: {}", payload);

        if (principal == null) {
            logger.warn("User not logged in - principal is null");
            return ResponseEntity.status(401).body(Map.of("error", "User not logged in"));
        }

        String username = principal.getName();
        logger.info("Processing for user: {}", username);

        Long productId = payload.get("productId");
        if (productId == null) {
            logger.warn("Product ID is null");
            return ResponseEntity.badRequest().body(Map.of("error", "Product ID is required"));
        }

        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            logger.error("User not found in database: {}", username);
            return ResponseEntity.status(401).body(Map.of("error", "User not found"));
        }

        Product product = productService.getProductById(productId).orElse(null);
        if (product == null) {
            logger.error("Product not found with ID: {}", productId);
            return ResponseEntity.notFound().build();
        }

        logger.info("Checking if favorite exists for user {} and product {}", user.getId(), product.getId());

        boolean isFavorite = favoriteService.isFavorite(user, product);
        logger.info("Current favorite status: {}", isFavorite);

        try {
            if (isFavorite) {
                logger.info("Removing favorite for user {} and product {}", user.getId(), product.getId());
                favoriteService.removeFavorite(user, product);
                return ResponseEntity.ok(Map.of("status", "removed"));
            } else {
                logger.info("Adding favorite for user {} and product {}", user.getId(), product.getId());
                favoriteService.addFavorite(user, product);
                return ResponseEntity.ok(Map.of("status", "added"));
            }
        } catch (Exception e) {
            logger.error("Error toggling favorite: ", e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @GetMapping("/ids")
    @ResponseBody
    public ResponseEntity<List<Long>> getFavoriteProductIds(Principal principal) {
        if (principal == null) {
            logger.info("User not logged in, returning empty favorites list");
            return ResponseEntity.ok(List.of());
        }

        String username = principal.getName();
        User user = userService.findByUsername(username).orElse(null);
        if (user == null) {
            logger.warn("User not found: {}", username);
            return ResponseEntity.ok(List.of());
        }

        List<Long> productIds = favoriteService.getUserFavorites(user).stream()
                .map(f -> f.getProduct().getId())
                .collect(Collectors.toList());

        logger.info("Returning {} favorite IDs for user {}", productIds.size(), username);
        return ResponseEntity.ok(productIds);
    }

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<List<Product>> getFavoriteProducts(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(List.of());
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }

        List<Product> favoriteProducts = favoriteService.getUserFavorites(user).stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favoriteProducts);
    }
}