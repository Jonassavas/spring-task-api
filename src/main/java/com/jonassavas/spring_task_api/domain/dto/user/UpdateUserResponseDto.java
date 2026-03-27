package com.jonassavas.spring_task_api.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserResponseDto {
    private UserDto user;
    private String token;
    private long expiresIn;
}
