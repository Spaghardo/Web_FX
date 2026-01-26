package com.example.hello.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Double price;
    private String category;
    
    @Column(length = 1000)
    private String description;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private com.example.hello.user.User seller;

    public Product() {}

    public Product(Long id, String title, Double price, String category, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.category = category;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public com.example.hello.user.User getSeller() { return seller; }
    public void setSeller(com.example.hello.user.User seller) { this.seller = seller; }
}
