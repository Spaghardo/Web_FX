package com.example.hello.controller;

import com.example.hello.model.Message;
import com.example.hello.model.Product;
import com.example.hello.repository.ProductRepository;
import com.example.hello.service.MessageService;
import com.example.hello.user.User;
import com.example.hello.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public String viewMessages(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return "redirect:/login";

        User currentUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (currentUser == null)
            return "redirect:/login";

        List<com.example.hello.dto.ConversationSummary> conversations = messageService.getConversations(currentUser);

        model.addAttribute("conversations", conversations);
        model.addAttribute("currentUser", currentUser);
        return "messages";
    }

    @GetMapping("/{otherUserId}")
    public String viewConversation(@PathVariable Long otherUserId,
            @RequestParam(required = false) Long productId,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return "redirect:/login";

        User currentUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
        if (currentUser == null)
            return "redirect:/login";

        User otherUser = userService.findById(otherUserId).orElse(null);

        if (otherUser == null) {
            return "redirect:/messages";
        }

        Product product = null;
        if (productId != null) {
            product = productRepository.findById(productId).orElse(null);
        }

        List<Message> conversation = messageService.getConversation(currentUser, otherUser, product);

        model.addAttribute("conversation", conversation);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute("product", product);

        return "conversation";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam Long receiverId,
            @RequestParam(required = false) Long productId,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return "redirect:/login";

        User currentUser = userService.findByUsername(userDetails.getUsername()).orElse(null);
        User receiver = userService.findById(receiverId).orElse(null);
        Product product = (productId != null) ? productRepository.findById(productId).orElse(null) : null;

        if (currentUser != null && receiver != null && content != null && !content.trim().isEmpty()) {
            messageService.sendMessage(currentUser, receiver, product, content);
        }

        String redirectUrl = "redirect:/messages/" + receiverId;
        if (productId != null) {
            redirectUrl += "?productId=" + productId;
        }

        return redirectUrl;
    }

    @GetMapping("/new")
    public String newMessage(@RequestParam Long recipientId,
            @RequestParam(required = false) Long productId,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return "redirect:/login";

        String redirectUrl = "redirect:/messages/" + recipientId;
        if (productId != null) {
            redirectUrl += "?productId=" + productId;
        }
        return redirectUrl;
    }
}
