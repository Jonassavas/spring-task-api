package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import com.jonassavas.spring_task_api.domain.dto.auth.RegisterRequest;
import com.jonassavas.spring_task_api.domain.entities.UserEntity;

public class TestAuthData {

    // Register DTOs ----------------------------------------------------------
    public static RegisterRequest createTestRegisterRequestDto(){
        return RegisterRequest.builder()
                                .username("testuser1")
                                .password("testpasswd1")
                                .email("test@email.com")
                                .build();
    }

    // Login DTOs -------------------------------------------------------------
    public static LoginRequest createTestLoginRequestDto(UserEntity user, String password){
        return LoginRequest.builder()
                                .username(user.getUsername())
                                .password(password)
                                .build();
    }
}
