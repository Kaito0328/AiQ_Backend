package com.example.myapp.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.myapp.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    List<User> findAll();
}
