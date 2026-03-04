package com.jonassavas.spring_task_api.services.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jonassavas.spring_task_api.domain.dto.auth.AuthResponse;
import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import com.jonassavas.spring_task_api.domain.dto.auth.RegisterRequest;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.spring_task_api.repositories.UserRepository;
import com.jonassavas.spring_task_api.services.AuthService;

public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authentificationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authentificationManager;
    }

    @Override
    public void register(RegisterRequest request) {

        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException(
                    "User already exists with username: " + request.getUsername());
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        // Encode the password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = 
                            authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()
                                )
                        );
        // Later generate JWT here
        return new AuthResponse("jwt-token will be here");
    }
    
}
