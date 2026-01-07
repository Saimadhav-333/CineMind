package com.netflix.auth_service.service;

import com.netflix.auth_service.model.User;
import com.netflix.auth_service.repository.UserRepository;
import com.netflix.auth_service.security.JwtUtil;
import com.netflix.auth_service.security.PasswordUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository repository,
                       PasswordUtil passwordUtil,
                       JwtUtil jwtUtil) {
        this.repository = repository;
        this.passwordUtil = passwordUtil;
        this.jwtUtil = jwtUtil;
    }

    public void register(String username, String password) {

        if (repository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordUtil.hash(password));
        user.setRole("USER");

        repository.save(user);
    }

    public String login(String username, String password) {

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordUtil.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }
}

