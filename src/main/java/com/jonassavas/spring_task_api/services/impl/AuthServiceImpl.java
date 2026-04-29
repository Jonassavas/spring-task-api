package com.jonassavas.spring_task_api.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jonassavas.spring_task_api.domain.dto.auth.AuthResponse;
import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import com.jonassavas.spring_task_api.domain.dto.auth.RegisterRequest;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.exceptions.ConflictFieldsException;
import com.jonassavas.spring_task_api.mappers.impl.user.UserMapper;
import com.jonassavas.spring_task_api.repositories.UserRepository;
import com.jonassavas.spring_task_api.security.JwtService;
import com.jonassavas.spring_task_api.services.AuthService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        Map<String, String> conflicts = new HashMap<>();

        if (userRepository.existsByUsername(request.getUsername())) {
            conflicts.put("username", "Username already in use");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            conflicts.put("email", "Email already in use");
        }
        if (!conflicts.isEmpty()) {
            throw new ConflictFieldsException(conflicts);
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserEntity savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getUsername());

        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationMs())
                .user(userMapper.mapTo(savedUser))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()));

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationMs())
                .user(userMapper.mapTo(user))
                .build();
    }
}
