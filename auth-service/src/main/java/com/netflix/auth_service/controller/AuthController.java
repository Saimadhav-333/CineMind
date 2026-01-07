package com.netflix.auth_service.controller;

import com.netflix.auth_service.dto.AuthResponse;
import com.netflix.auth_service.dto.LoginRequest;
import com.netflix.auth_service.dto.RegisterRequest;
import com.netflix.auth_service.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest req) {
        service.register(req.getUsername(), req.getPassword());
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        String token = service.login(req.getUsername(), req.getPassword());
        return new AuthResponse(token);
    }
}

