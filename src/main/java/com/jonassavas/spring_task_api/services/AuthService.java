package com.jonassavas.spring_task_api.services;

import com.jonassavas.spring_task_api.domain.dto.auth.AuthResponse;
import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import com.jonassavas.spring_task_api.domain.dto.auth.RegisterRequest;

public interface AuthService {
    // Authenticate user, generate JWT, return Token.

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
