package com.akertesz.task_manager_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.akertesz.task_manager_api.dto.LoginRequest;
import com.akertesz.task_manager_api.dto.LoginResponse;
import com.akertesz.task_manager_api.dto.RegisterRequest;
import com.akertesz.task_manager_api.service.UserService;

@RestController
@RequestMapping("/api/login")
public class UserController {
    private final UserService loginService;

    @Autowired
    public UserController(UserService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request.getUsername(), request.getPassword()));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(loginService.register(request.getUsername(), request.getPassword()));
    }
}   
