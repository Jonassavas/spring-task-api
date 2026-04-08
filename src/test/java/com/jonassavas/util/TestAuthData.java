package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import com.jonassavas.spring_task_api.domain.dto.auth.RegisterRequest;

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
    public static LoginRequest createTestLoginRequestDto(String username, String password){
        return LoginRequest.builder()
                                .username(username)
                                .password(password)
                                .build();
    }
}
