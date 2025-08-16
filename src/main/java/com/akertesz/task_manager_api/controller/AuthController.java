package com.akertesz.task_manager_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akertesz.task_manager_api.dto.LoginRequest;
import com.akertesz.task_manager_api.dto.LoginResponse;
import com.akertesz.task_manager_api.dto.RegisterRequest;
import com.akertesz.task_manager_api.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse response = userService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
    
    // Explicitly handle unsupported methods to return 405
    @GetMapping("/login")
    public ResponseEntity<Void> loginGet() {
        return ResponseEntity.status(405).build();
    }
    
    @GetMapping("/register")
    public ResponseEntity<Void> registerGet() {
        return ResponseEntity.status(405).build();
    }
    
    @PutMapping("/login")
    public ResponseEntity<Void> loginPut() {
        return ResponseEntity.status(405).build();
    }
    
    @PutMapping("/register")
    public ResponseEntity<Void> registerPut() {
        return ResponseEntity.status(405).build();
    }
    
    @DeleteMapping("/login")
    public ResponseEntity<Void> loginDelete() {
        return ResponseEntity.status(405).build();
    }
    
    @DeleteMapping("/register")
    public ResponseEntity<Void> registerDelete() {
        return ResponseEntity.status(405).build();
    }
}
