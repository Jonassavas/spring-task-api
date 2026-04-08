package com.jonassavas.util;

import com.jonassavas.spring_task_api.domain.dto.auth.LoginRequest;
import com.jonassavas.spring_task_api.domain.dto.auth.RegisterRequest;
import java.util.UUID;

public class TestAuthData {

    // Register DTOs ----------------------------------------------------------
    public static RegisterRequest createTestRegisterRequestDto() {
        String unique =
                UUID.randomUUID().toString().replace("-", "").substring(0, 8); // short + safe

        return RegisterRequest.builder()
                .username("user_" + unique)
                .password("testpasswd1")
                .email("email_" + unique + "@test.com")
                .build();
    }

    // Login DTOs -------------------------------------------------------------
    public static LoginRequest createTestLoginRequestDto(String username, String password) {
        return LoginRequest.builder().username(username).password(password).build();
    }
}
