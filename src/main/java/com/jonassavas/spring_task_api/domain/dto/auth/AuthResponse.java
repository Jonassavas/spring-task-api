package com.jonassavas.spring_task_api.domain.dto.auth;

import com.jonassavas.spring_task_api.domain.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private long expiresIn;
    private UserDto user;
}
