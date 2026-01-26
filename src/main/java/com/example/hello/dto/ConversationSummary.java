package com.example.hello.dto;

import com.example.hello.model.Message;
import com.example.hello.model.Product;
import com.example.hello.user.User;

public class ConversationSummary {
    private User partner;
    private Product product;
    private Message lastMessage;

    public ConversationSummary(User partner, Product product, Message lastMessage) {
        this.partner = partner;
        this.product = product;
        this.lastMessage = lastMessage;
    }

    public User getPartner() {
        return partner;
    }

    public void setPartner(User partner) {
        this.partner = partner;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }
}
