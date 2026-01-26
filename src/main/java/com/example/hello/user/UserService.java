package com.example.hello.user;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(String username, String password, String fullName, String roleName) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        Role role = Role.valueOf(roleName);
        
        // All users (Buyers, Sellers, Admins) are automatically approved
        User user = new User(username, passwordEncoder.encode(password), fullName, 
                           Collections.singleton(role), AccountStatus.APPROVED, true);
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // Check if account is enabled
        if (!user.isEnabled()) {
            throw new RuntimeException("Account is pending verification. Please wait for admin approval.");
        }
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList())
        );
    }


    public java.util.List<User> getPendingSellers() {
        return userRepository.findByRolesContainingAndAccountStatus(Role.SELLER, AccountStatus.PENDING);
    }

    public java.util.List<User> getAllSellers() {
        return userRepository.findByRolesContaining(Role.SELLER);
    }

    public void approveSeller(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountStatus(AccountStatus.APPROVED);
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void rejectSeller(Long userId) {
        // Simply delete the user account
        userRepository.deleteById(userId);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
