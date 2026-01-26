package com.example.hello.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByRolesContaining(Role role);

    List<User> findByRolesContainingAndAccountStatus(Role role, AccountStatus status);
}
