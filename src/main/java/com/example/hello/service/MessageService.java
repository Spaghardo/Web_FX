package com.example.hello.service;

import com.example.hello.model.Message;
import com.example.hello.model.Product;
import com.example.hello.repository.MessageRepository;
import com.example.hello.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(User sender, User receiver, Product product, String content) {
        Message message = new Message(sender, receiver, product, content);
        return messageRepository.save(message);
    }

    public List<Message> getConversation(User user1, User user2, Product product) {
        List<Message> allMessages = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(user1, user2, user1, user2);
        if (product == null) {
            return allMessages.stream().filter(m -> m.getProduct() == null).collect(Collectors.toList());
        }
        return allMessages.stream()
                .filter(m -> m.getProduct() != null && m.getProduct().getId().equals(product.getId()))
                .collect(Collectors.toList());
    }

    public List<com.example.hello.dto.ConversationSummary> getConversations(User user) {
        List<Message> allMessages = messageRepository.findBySenderOrReceiverOrderByTimestampDesc(user, user);
        
        // Group by Key(Partner, Product)
        Map<String, com.example.hello.dto.ConversationSummary> map = new LinkedHashMap<>();
        
        for (Message m : allMessages) {
            User partner = m.getSender().getId().equals(user.getId()) ? m.getReceiver() : m.getSender();
            Product product = m.getProduct();
            String key = partner.getId() + "_" + (product != null ? product.getId() : "null");
            
            if (!map.containsKey(key)) {
                map.put(key, new com.example.hello.dto.ConversationSummary(partner, product, m));
            }
        }
        
        return new ArrayList<>(map.values());
    }
}
