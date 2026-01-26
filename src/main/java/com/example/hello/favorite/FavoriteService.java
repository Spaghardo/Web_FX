package com.example.hello.favorite;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hello.model.Product;
import com.example.hello.user.User;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public List<Favorite> getUserFavorites(User user) {
        return favoriteRepository.findByUser(user);
    }

    @Transactional
    public void addFavorite(User user, Product product) {
        if (!favoriteRepository.existsByUserAndProduct(user, product)) {
            Favorite favorite = new Favorite(user, product);
            favoriteRepository.save(favorite);
        }
    }

    @Transactional
    public void removeFavorite(User user, Product product) {
        favoriteRepository.deleteByUserAndProduct(user, product);
    }

    public boolean isFavorite(User user, Product product) {
        return favoriteRepository.existsByUserAndProduct(user, product);
    }
}
