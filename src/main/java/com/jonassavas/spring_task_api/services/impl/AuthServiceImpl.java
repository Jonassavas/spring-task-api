package com.jonassavas.spring_task_api.services.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.auth.AuthResponse;
import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import com.jonassavas.spring_task_api.domain.dto.auth.RegisterRequest;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.repositories.UserRepository;
import com.jonassavas.spring_task_api.security.JwtService;
import com.jonassavas.spring_task_api.services.AuthService;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authentificationManager,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authentificationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException(
                    "Username already taken: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(
                    "Email already taken: " + request.getEmail());
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        String token = jwtService.generateToken(request.getUsername());

        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }
    
}
