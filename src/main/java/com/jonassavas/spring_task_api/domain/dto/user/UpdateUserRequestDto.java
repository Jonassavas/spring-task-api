package com.jonassavas.spring_task_api.domain.dto.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequestDto {

    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email cannot exceed 254 characters")
    private String email;
}
