package com.jonassavas.spring_task_api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import com.jonassavas.spring_task_api.domain.dto.user.UserRegisterRequestDto;
import com.jonassavas.spring_task_api.services.AuthService;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequestDto requestDto){
        authService.register(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest requestDto){

    }
}
